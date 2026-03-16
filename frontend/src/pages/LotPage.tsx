import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import axios from 'axios';
import api from '../api/axios';
import type { Lot } from '../types';
import { createStompClient } from '../api/websocket';
import { useAuthStore } from '../store/useAuthStore';
import { Gavel, Clock, TrendingUp } from 'lucide-react';

export const LotPage = () => {
    // 1. Строгая типизация параметров URL
    const { id } = useParams<{ id: string }>();
    const { user } = useAuthStore();

    const [lot, setLot] = useState<Lot | null>(null);
    const [bidAmount, setBidAmount] = useState<number>(0);
    const [message, setMessage] = useState({ text: '', type: '' });

    useEffect(() => {
        if (!id) return;

        api.get<Lot>(`/lots/${id}`).then(res => {
            setLot(res.data);
            setBidAmount(res.data.currentPrice + 50);
        });

        const stompClient = createStompClient((updatedLot: Lot) => {
            setLot(updatedLot);
        }, Number(id));

        stompClient.activate();

        return () => {
            void stompClient.deactivate();
        };
    }, [id]);

    const handleBid = async (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        try {
            await api.post('/bids', { lotId: id, amount: bidAmount });
            setMessage({ text: 'Ставка принята!', type: 'success' });
        } catch (err: unknown) {
            // Используем axios.isAxiosError вместо 'any'
            if (axios.isAxiosError(err)) {
                setMessage({
                    text: err.response?.data?.error || 'Ошибка при ставке',
                    type: 'error'
                });
            }
        }
    };

    const handleAutoBid = async () => {
        const input = document.getElementById('autoBidLimit') as HTMLInputElement;
        const limit = input?.value;

        if (!id || !limit) return;

        try {
            await api.post(`/bids/auto/${id}?maxLimit=${limit}`);
            setMessage({ text: 'Авто-биддер активирован!', type: 'success' });
        } catch (err: unknown) {
            if (axios.isAxiosError(err)) {
                setMessage({
                    text: err.response?.data?.error || 'Ошибка активации',
                    type: 'error'
                });
            }
        }
    };

    if (!lot) return <div className="p-10 text-center">Загрузка лота...</div>;

    return (
        <div className="max-w-5xl mx-auto grid grid-cols-1 md:grid-cols-2 gap-10 p-4">
            <div className="bg-gray-100 rounded-2xl h-96 flex items-center justify-center overflow-hidden">
                <img
                    src={`https://source.unsplash.com/random/800x600/?auction,${lot.title}`}
                    alt="Lot"
                    className="object-cover w-full h-full opacity-90"
                    onError={(e) => (e.currentTarget.src = 'https://via.placeholder.com/800x600?text=No+Image')}
                />
            </div>

            <div className="flex flex-col space-y-6">
                <h1 className="text-4xl font-bold text-gray-900">{lot.title}</h1>
                <button
                    onClick={() => {
                        window.open(`http://localhost:8080/api/v1/lots/${lot.id}/report`, '_blank');
                    }}
                    className="mt-4 text-sm text-blue-600 hover:underline flex items-center gap-1"
                >
                    Скачать PDF отчет
                </button>
                <div className="flex items-center gap-6 p-4 bg-white rounded-xl shadow-sm border border-gray-100">
                    <div className="flex items-center gap-2 text-blue-600">
                        <TrendingUp size={24} />
                        <div>
                            <p className="text-xs text-gray-500 uppercase font-bold">Текущая цена</p>
                            <p className="text-3xl font-black">${lot.currentPrice.toFixed(2)}</p>
                            {lot.eurPrice && (
                                <p className="text-sm text-gray-500 font-medium">≈ €{lot.eurPrice.toFixed(2)}</p>
                            )}
                        </div>
                    </div>
                    <div className="h-10 w-px bg-gray-200"></div>
                    <div className="flex items-center gap-2 text-gray-700">
                        <Clock size={24} />
                        <div>
                            <p className="text-xs text-gray-500 uppercase font-bold">Завершение</p>
                            <p className="font-semibold">{new Date(lot.endTime).toLocaleTimeString()}</p>
                        </div>
                    </div>
                </div>

                {user ? (
                    <>
                        <form onSubmit={handleBid} className="p-6 bg-gray-900 rounded-2xl text-white shadow-xl">
                            <h3 className="text-lg font-bold mb-4 flex items-center gap-2">
                                <Gavel size={20} /> Ваша ставка
                            </h3>
                            <div className="flex gap-3">
                                <input
                                    type="number"
                                    className="flex-1 bg-gray-800 border border-gray-700 rounded-lg p-3 text-xl font-bold focus:ring-2 focus:ring-blue-500 outline-none"
                                    value={bidAmount}
                                    onChange={(e) => setBidAmount(Number(e.target.value))}
                                />
                                <button type="submit" className="bg-blue-600 hover:bg-blue-500 px-8 py-3 rounded-lg font-bold transition">
                                    Подтвердить
                                </button>
                            </div>
                        </form>

                        <div className="mt-8 p-6 bg-white border-2 border-dashed border-blue-200 rounded-2xl">
                            <h3 className="text-lg font-bold mb-2 text-blue-800 flex items-center gap-2">
                                <TrendingUp size={20} /> Авто-биддер
                            </h3>
                            <div className="flex gap-3">
                                <input
                                    type="number"
                                    placeholder="Ваш лимит ($)"
                                    className="flex-1 bg-gray-50 border border-gray-300 rounded-lg p-3 font-semibold focus:ring-2 focus:ring-blue-500 outline-none"
                                    id="autoBidLimit"
                                />
                                <button
                                    onClick={handleAutoBid}
                                    className="bg-blue-100 text-blue-700 hover:bg-blue-200 px-6 py-3 rounded-lg font-bold transition"
                                >
                                    Установить
                                </button>
                            </div>
                        </div>
                    </>
                ) : (
                    <div className="p-6 bg-blue-50 border border-blue-100 rounded-xl text-blue-700">
                        Пожалуйста, войдите, чтобы участвовать в торгах.
                    </div>
                )}

                {message.text && (
                    <p className={`p-3 rounded-lg text-sm font-medium ${message.type === 'success' ? 'bg-green-100 text-green-700' : 'bg-red-100 text-red-700'}`}>
                        {message.text}
                    </p>
                )}
            </div>
        </div>
    );
};
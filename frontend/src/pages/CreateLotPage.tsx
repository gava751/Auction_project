import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import api from '../api/axios';
import { useAuthStore } from '../store/useAuthStore';
import { PackagePlus } from 'lucide-react';

interface Category {
    id: number;
    name: string;
}

export const CreateLotPage = () => {
    const navigate = useNavigate();
    const { user } = useAuthStore();

    const [title, setTitle] = useState('');
    const [description, setDescription] = useState('');
    const [startPrice, setStartPrice] = useState('');
    const [bidStep, setBidStep] = useState('');
    const [durationDays, setDurationDays] = useState('7');
    const [file, setFile] = useState<File | null>(null);
    const [categories, setCategories] = useState<Category[]>([]);
    const [categoryId, setCategoryId] = useState('');

    const [error, setError] = useState('');
    const [isLoading, setIsLoading] = useState(false);

    useEffect(() => {
        api.get<Category[]>('/categories')
            .then(res => {
                setCategories(res.data);
                if (res.data.length > 0) {
                    setCategoryId(String(res.data[0].id));
                }
            })
            .catch(err => console.error("Ошибка загрузки категорий:", err));
    }, []);

    if (user?.role !== 'ROLE_SELLER') {
        return (
            <div className="text-center mt-20 text-red-600">
                <h1 className="text-2xl font-bold">Доступ запрещен</h1>
                <p>Эта страница доступна только для пользователей с ролью Продавец.</p>
            </div>
        );
    }

    const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        setIsLoading(true);

        const endDate = new Date();
        endDate.setDate(endDate.getDate() + parseInt(durationDays));
        const formattedEndTime = endDate.toISOString().slice(0, 19);

        const formData = new FormData();
        formData.append('title', title);
        formData.append('description', description);
        formData.append('startPrice', startPrice);
        formData.append('bidStep', bidStep);
        formData.append('endTime', formattedEndTime);
        formData.append('categoryId', categoryId);
        if (file) formData.append('file', file);

        try {
            await api.post('/lots', formData, {
                headers: { 'Content-Type': 'multipart/form-data' }
            });
            navigate('/');
        } catch (err: unknown) {
            if (axios.isAxiosError(err)) {
                setError(err.response?.data?.error || err.response?.data || 'Ошибка создания лота');
            } else {
                setError('Произошла непредвиденная ошибка');
            }
        } finally {
            setIsLoading(false);
        }
    };

    return (
        <div className="max-w-2xl mx-auto mt-10 p-8 bg-white rounded-2xl shadow-sm border border-gray-200 mb-20">
            <div className="flex items-center gap-3 mb-6 border-b pb-4">
                <PackagePlus className="text-green-600" size={32} />
                <h1 className="text-2xl font-bold text-gray-800">Выставить новый лот</h1>
            </div>

            {error && (
                <div className="bg-red-50 text-red-700 p-4 rounded-lg mb-6 border border-red-100 text-sm font-medium">
                    {error}
                </div>
            )}

            <form onSubmit={handleSubmit} className="space-y-6">
                <div>
                    <label className="block text-sm font-bold text-gray-700 mb-1 text-left">Название лота</label>
                    <input
                        type="text"
                        required
                        className="w-full p-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-green-500 outline-none"
                        value={title}
                        onChange={(e) => setTitle(e.target.value)}
                        placeholder="Например: Игровая приставка Sony PlayStation 5"
                    />
                </div>
                <div>
                    <label className="block text-sm font-bold text-gray-700 mb-1">Фото товара</label>
                    <input
                        type="file" accept="image/*"
                        className="w-full p-2 border border-gray-300 rounded-lg bg-gray-50"
                        onChange={(e) => setFile(e.target.files ? e.target.files[0] : null)}
                    />
                </div>
                <div>
                    <label className="block text-sm font-bold text-gray-700 mb-1 text-left">Описание</label>
                    <textarea
                        className="w-full p-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-green-500 outline-none h-28"
                        value={description}
                        onChange={(e) => setDescription(e.target.value)}
                        placeholder="Опишите состояние товара, комплектность и нюансы..."
                    />
                </div>

                <div className="grid grid-cols-1 md:grid-cols-2 gap-6 text-left">
                    <div>
                        <label className="block text-sm font-bold text-gray-700 mb-1">Стартовая цена ($)</label>
                        <input
                            type="number"
                            min="0.01"
                            step="0.01"
                            required
                            className="w-full p-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-green-500 outline-none"
                            value={startPrice}
                            onChange={(e) => setStartPrice(e.target.value)}
                        />
                    </div>
                    <div>
                        <label className="block text-sm font-bold text-gray-700 mb-1">Минимальный шаг ставки ($)</label>
                        <input
                            type="number"
                            min="0.01"
                            step="0.01"
                            required
                            className="w-full p-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-green-500 outline-none"
                            value={bidStep}
                            onChange={(e) => setBidStep(e.target.value)}
                        />
                    </div>
                </div>

                <div className="grid grid-cols-1 md:grid-cols-2 gap-6 text-left">
                    <div>
                        <label className="block text-sm font-bold text-gray-700 mb-1">Длительность аукциона</label>
                        <select
                            className="w-full p-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-green-500 outline-none bg-white cursor-pointer"
                            value={durationDays}
                            onChange={(e) => setDurationDays(e.target.value)}
                        >
                            <option value="1">1 день (Быстрые торги)</option>
                            <option value="3">3 дня</option>
                            <option value="7">7 дней (Рекомендуется)</option>
                            <option value="14">14 дней (Долгосрочно)</option>
                        </select>
                    </div>
                    <div>
                        <label className="block text-sm font-bold text-gray-700 mb-1">Категория</label>
                        <select
                            className="w-full p-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-green-500 outline-none bg-white cursor-pointer"
                            value={categoryId}
                            onChange={(e) => setCategoryId(e.target.value)}
                        >
                            {categories.map((cat) => (
                                <option key={cat.id} value={cat.id}>{cat.name}</option>
                            ))}
                        </select>
                    </div>
                </div>

                <div className="pt-6 border-t mt-4">
                    <button
                        type="submit"
                        disabled={isLoading}
                        className={`w-full bg-green-600 text-white font-bold py-3 rounded-xl shadow-lg hover:bg-green-700 hover:-translate-y-0.5 active:translate-y-0 transition-all duration-200 ${isLoading ? 'opacity-50' : ''}`}
                    >
                        {isLoading ? 'Публикуем...' : 'Разместить аукцион'}
                    </button>
                </div>
            </form>
        </div>
    );
};
import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../api/axios';
import { useAuthStore } from '../store/useAuthStore';
import { ShieldCheck } from 'lucide-react';

export const ApplySellerPage = () => {
    const { user } = useAuthStore();
    const navigate = useNavigate();

    const[passportData, setPassportData] = useState('');
    const [file, setFile] = useState<File | null>(null);
    const[message, setMessage] = useState('');
    const [isLoading, setIsLoading] = useState(false);

    if (user?.role !== 'ROLE_BUYER') {
        return <div className="text-center p-10">Эта страница только для покупателей.</div>;
    }

    const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        if (!file) {
            setMessage('Пожалуйста, прикрепите фото паспорта');
            return;
        }

        setIsLoading(true);
        const formData = new FormData();
        formData.append('passportData', passportData);
        formData.append('file', file);

        try {
            await api.post('/applications', formData, {
                headers: { 'Content-Type': 'multipart/form-data' }
            });
            setMessage('Ваша заявка успешно отправлена! Ожидайте решения модератора.');
            setTimeout(() => navigate('/'), 3000);
        } catch (err: any) {
            setMessage(err.response?.data || 'Ошибка отправки заявки');
        } finally {
            setIsLoading(false);
        }
    };

    return (
        <div className="max-w-lg mx-auto mt-10 p-8 bg-white rounded-2xl shadow-sm border border-gray-200">
            <div className="flex items-center gap-3 mb-6 border-b pb-4">
                <ShieldCheck className="text-blue-600" size={32} />
                <h1 className="text-2xl font-bold text-gray-800">Стать продавцом</h1>
            </div>

            <p className="text-gray-600 mb-6 text-sm">
                Для обеспечения безопасности сделок, публикация лотов доступна только верифицированным пользователям. Пожалуйста, укажите ваши данные.
            </p>

            {message && <div className="bg-blue-50 text-blue-700 p-4 rounded-lg mb-6 text-sm font-bold">{message}</div>}

            <form onSubmit={handleSubmit} className="space-y-6">
                <div>
                    <label className="block text-sm font-bold text-gray-700 mb-1">Серия и номер паспорта</label>
                    <input
                        type="text" required
                        className="w-full p-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
                        value={passportData}
                        onChange={(e) => setPassportData(e.target.value)}
                        placeholder="Например: MC 1234567"
                    />
                </div>

                <div>
                    <label className="block text-sm font-bold text-gray-700 mb-1">Скан или фото паспорта</label>
                    <input
                        type="file" required accept="image/*,.pdf"
                        className="w-full p-2 border border-gray-300 rounded-lg bg-gray-50"
                        onChange={(e) => setFile(e.target.files ? e.target.files[0] : null)}
                    />
                </div>

                <button
                    type="submit" disabled={isLoading}
                    className="w-full bg-blue-600 text-white font-bold py-3 rounded-xl hover:bg-blue-700 transition"
                >
                    {isLoading ? 'Отправка...' : 'Отправить на проверку'}
                </button>
            </form>
        </div>
    );
};
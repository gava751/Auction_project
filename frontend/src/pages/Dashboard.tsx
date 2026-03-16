import { useEffect, useState } from 'react';
import api from '../api/axios';
import { useAuthStore } from '../store/useAuthStore';
import { Link } from 'react-router-dom';

interface SellerApplication {
    id: number;
    user: {
        email: string;
    };
    passportData: string;
    documentPath: string;
    status: string;
}

export const Dashboard = () => {
    const { user } = useAuthStore();
    const [applications, setApplications] = useState<SellerApplication[]>([]);

    useEffect(() => {
        if (user?.role === 'ROLE_ADMIN') {
            api.get<SellerApplication[]>('/admin/applications')
                .then(res => setApplications(res.data))
                .catch(err => console.error("Ошибка загрузки заявок", err));
        }
    }, [user]);

    const handleApprove = async (id: number) => {
        try {
            await api.post(`/admin/applications/${id}/approve`);
            setApplications(applications.filter(app => app.id !== id));
            alert('Пользователь успешно переведен в Продавцы!');
        } catch (err) {
            console.error("Ошибка при одобрении", err);
            alert('Не удалось одобрить заявку');
        }
    };

    const handleReject = async (id: number) => {
        try {
            await api.post(`/admin/applications/${id}/reject`);
            setApplications(applications.filter(app => app.id !== id));
        } catch (err) {
            console.error("Ошибка при отклонении", err);
        }
    };

    return (
        <div className="p-8 max-w-5xl mx-auto mb-20">
            <h1 className="text-3xl font-bold mb-6">Панель управления</h1>

            {user?.role === 'ROLE_ADMIN' && (
                <div className="bg-white p-6 rounded-xl shadow-sm border border-red-100">
                    <h2 className="text-xl font-bold text-red-700 mb-4">Заявки на верификацию (KYC)</h2>

                    {applications.length === 0 ? (
                        <p className="text-gray-500">Новых заявок нет.</p>
                    ) : (
                        <div className="space-y-4">
                            {applications.map(app => (
                                <div key={app.id} className="p-4 border rounded-lg flex flex-col md:flex-row md:justify-between md:items-center bg-gray-50 gap-4">
                                    <div>
                                        <p className="font-bold">Пользователь: <span className="text-blue-600">{app.user.email}</span></p>
                                        <p className="text-sm text-gray-600 mt-1">Паспорт: <span className="font-mono bg-gray-200 px-1 rounded">{app.passportData}</span></p>
                                        <p className="text-xs text-gray-400 mt-1">Файл: {app.documentPath}</p>
                                    </div>
                                    <div className="flex gap-2 w-full md:w-auto">
                                        <button
                                            onClick={() => handleApprove(app.id)}
                                            className="flex-1 md:flex-none bg-green-600 text-white px-4 py-2 rounded-lg text-sm font-bold hover:bg-green-700 transition"
                                        >
                                            Одобрить
                                        </button>
                                        <button
                                            onClick={() => handleReject(app.id)}
                                            className="flex-1 md:flex-none bg-red-600 text-white px-4 py-2 rounded-lg text-sm font-bold hover:bg-red-700 transition"
                                        >
                                            Отклонить
                                        </button>
                                    </div>
                                </div>
                            ))}
                        </div>
                    )}
                </div>
            )}

            {user?.role === 'ROLE_SELLER' && (
                <div className="bg-white p-6 rounded-xl shadow-sm border border-green-100">
                    <h2 className="text-xl font-bold text-green-700 mb-4">Кабинет Продавца</h2>
                    <Link
                        to="/create-lot"
                        className="inline-block bg-green-600 text-white px-6 py-2 rounded-lg font-bold hover:bg-green-700 transition"
                    >
                        Создать новый лот
                    </Link>
                    <div className="mt-6 border-t pt-6">
                        <h3 className="font-bold mb-2">Ваши возможности:</h3>
                        <p className="text-sm text-gray-600">
                            Как верифицированный продавец, вы можете размещать неограниченное количество лотов.
                            Перейдите в каталог, чтобы посмотреть уже активные аукционы.
                        </p>
                    </div>
                </div>
            )}
        </div>
    );
};
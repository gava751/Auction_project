import { useAuthStore } from '../store/useAuthStore';

export const Dashboard = () => {
    const { user } = useAuthStore();

    return (
        <div className="p-8">
            <h1 className="text-3xl font-bold mb-6">Панель управления</h1>

            {user?.role === 'ROLE_ADMIN' && (
                <div className="bg-white p-6 rounded-xl shadow-sm border border-red-100">
                    <h2 className="text-xl font-bold text-red-700 mb-4">Инструменты Администратора</h2>
                    <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                        <div className="p-4 bg-gray-50 rounded-lg border">
                            <p className="font-bold">Модерация пользователей</p>
                            <p className="text-sm text-gray-500">Список всех регистраций и кнопка блокировки.</p>
                            <button className="mt-2 text-blue-600 text-sm font-bold">Открыть список →</button>
                        </div>
                        <div className="p-4 bg-gray-50 rounded-lg border">
                            <p className="font-bold">Управление категориями</p>
                            <p className="text-sm text-gray-500">Добавление и удаление категорий товаров.</p>
                            <button className="mt-2 text-blue-600 text-sm font-bold">Настроить →</button>
                        </div>
                    </div>
                </div>
            )}

            {user?.role === 'ROLE_SELLER' && (
                <div className="bg-white p-6 rounded-xl shadow-sm border border-green-100">
                    <h2 className="text-xl font-bold text-green-700 mb-4">Кабинет Продавца</h2>
                    <button className="bg-green-600 text-white px-6 py-2 rounded-lg font-bold hover:bg-green-700 transition">
                        Создать новый лот
                    </button>
                    <div className="mt-6 border-t pt-6">
                        <h3 className="font-bold mb-2">Ваши активные лоты:</h3>
                        <p className="text-sm text-gray-400">У вас пока нет активных лотов.</p>
                    </div>
                </div>
            )}
        </div>
    );
};
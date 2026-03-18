import { useEffect, useState } from 'react';
import api from '../api/axios';
import type { Lot, PageResponse } from '../types';
import { LotCard } from '../components/LotCard';
import { useAuthStore } from '../store/useAuthStore';
import { Search, Filter } from 'lucide-react';

interface Category {
    id: number;
    name: string;
}

export const HomePage = () => {
    const { user } = useAuthStore();
    const [lots, setLots] = useState<Lot[]>([]);
    const [categories, setCategories] = useState<Category[]>([]);
    const[loading, setLoading] = useState(true);
    const [error, setError] = useState('');

    const[searchQuery, setSearchQuery] = useState('');
    const [selectedCategory, setSelectedCategory] = useState('');

    useEffect(() => {
        api.get<Category[]>('/categories')
            .then(res => setCategories(res.data))
            .catch(console.error);
    },[]);

    useEffect(() => {
        const fetchLots = async () => {
            setLoading(true);
            try {
                let url = `/lots?page=0&size=12`;
                if (searchQuery) url += `&search=${encodeURIComponent(searchQuery)}`;
                if (selectedCategory) url += `&categoryId=${selectedCategory}`;

                const response = await api.get<PageResponse<Lot>>(url);
                setLots(response.data.content);
            } catch (err) {
                console.error(err);
                setError('Не удалось загрузить каталог лотов');
            } finally {
                setLoading(false);
            }
        };

        const delayDebounceFn = setTimeout(() => {
            fetchLots();
        }, 300);

        return () => clearTimeout(delayDebounceFn);
    },[searchQuery, selectedCategory, user]);

    return (
        <div className="px-4 pb-20">
            <div className="flex flex-col md:flex-row justify-between items-start md:items-center mb-8 gap-4">
                <h1 className="text-4xl font-black text-gray-900 tracking-tight">Активные аукционы</h1>
                <div className="text-sm text-gray-500 bg-white px-4 py-2 rounded-full shadow-sm border border-gray-200 font-bold">
                    {lots.length} лотов найдено
                </div>
            </div>

            <div className="flex flex-col md:flex-row gap-4 mb-10 bg-white p-4 rounded-2xl shadow-sm border border-gray-100">
                <div className="flex-1 relative">
                    <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                        <Search className="text-gray-400" size={20} />
                    </div>
                    <input
                        type="text"
                        className="w-full pl-10 pr-4 py-3 bg-gray-50 border border-gray-200 rounded-xl focus:ring-2 focus:ring-blue-500 outline-none transition"
                        placeholder="Искать по названию лота..."
                        value={searchQuery}
                        onChange={(e) => setSearchQuery(e.target.value)}
                    />
                </div>

                <div className="md:w-64 relative">
                    <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                        <Filter className="text-gray-400" size={20} />
                    </div>
                    <select
                        className="w-full pl-10 pr-4 py-3 bg-gray-50 border border-gray-200 rounded-xl focus:ring-2 focus:ring-blue-500 outline-none transition appearance-none cursor-pointer"
                        value={selectedCategory}
                        onChange={(e) => setSelectedCategory(e.target.value)}
                    >
                        <option value="">Все категории</option>
                        {categories.map(cat => (
                            <option key={cat.id} value={cat.id}>{cat.name}</option>
                        ))}
                    </select>
                </div>
            </div>

            {error && <div className="text-center py-10 text-red-500 font-medium">{error}</div>}

            {loading ? (
                <div className="flex justify-center items-center h-64">
                    <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
                </div>
            ) : lots.length === 0 ? (
                <div className="text-center py-24 bg-white rounded-3xl border-2 border-dashed border-gray-200">
                    <p className="text-xl font-bold text-gray-400 mb-2">Ничего не найдено</p>
                    <p className="text-gray-500">Попробуйте изменить параметры поиска или выбрать другую категорию.</p>
                </div>
            ) : (
                <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-8">
                    {lots.map(lot => (
                        <LotCard key={lot.id} lot={lot} />
                    ))}
                </div>
            )}
        </div>
    );
};
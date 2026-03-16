import { useEffect, useState } from 'react';
import api from '../api/axios';
import type {Lot, PageResponse} from '../types';
import { LotCard } from '../components/LotCard';

export const HomePage = () => {
    const [lots, setLots] = useState<Lot[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');

    useEffect(() => {
        const fetchLots = async () => {
            try {
                const response = await api.get<PageResponse<Lot>>('/lots?page=0&size=12');
                setLots(response.data.content);
            } catch (err) {
                console.error(err);
                setError('Не удалось загрузить каталог лотов');
            } finally {
                setLoading(false);
            }
        };

        fetchLots();
    }, []);

    if (loading) return (
        <div className="flex justify-center items-center h-64">
            <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
        </div>
    );

    if (error) return (
        <div className="text-center py-20 text-red-500 font-medium">{error}</div>
    );

    return (
        <div className="px-4">
            <div className="flex justify-between items-center mb-8">
                <h1 className="text-3xl font-extrabold text-gray-900 tracking-tight">Активные аукционы</h1>
                <div className="text-sm text-gray-500 bg-white px-3 py-1 rounded-full shadow-sm border border-gray-100">
                    {lots.length} лотов доступно
                </div>
            </div>

            {lots.length === 0 ? (
                <div className="text-center py-20 bg-white rounded-2xl border border-dashed border-gray-300">
                    <p className="text-gray-500">На данный момент нет активных торгов.</p>
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
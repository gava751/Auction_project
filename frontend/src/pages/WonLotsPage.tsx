import { useEffect, useState } from 'react';
import api from '../api/axios';
import type { Lot } from '../types';
import { LotCard } from '../components/LotCard';
import { Trophy } from 'lucide-react';

export const WonLotsPage = () => {
    const [wonLots, setWonLots] = useState<Lot[]>([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        api.get<Lot[]>('/lots/won')
            .then(res => setWonLots(res.data))
            .finally(() => setLoading(false));
    }, []);

    if (loading) return <div className="p-10 text-center text-gray-500">Загрузка ваших побед...</div>;

    return (
        <div className="px-4 pb-20">
            <div className="flex items-center gap-3 mb-8">
                <Trophy className="text-yellow-500" size={32} />
                <h1 className="text-4xl font-black text-gray-900 tracking-tight">Мои победы</h1>
            </div>

            {wonLots.length === 0 ? (
                <div className="text-center py-24 bg-white rounded-3xl border-2 border-dashed border-gray-200">
                    <p className="text-xl font-bold text-gray-400 mb-2">У вас пока нет выигранных лотов</p>
                    <p className="text-gray-500">Участвуйте в торгах, чтобы увидеть их здесь!</p>
                </div>
            ) : (
                <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-8">
                    {wonLots.map(lot => (
                        <LotCard key={lot.id} lot={lot} />
                    ))}
                </div>
            )}
        </div>
    );
};
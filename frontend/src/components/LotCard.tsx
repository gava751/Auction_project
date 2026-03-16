import { Link } from 'react-router-dom';
import type {Lot} from '../types';

interface LotCardProps {
    lot: Lot;
}

export const LotCard = ({ lot }: LotCardProps) => {
    const formatEndTime = (dateString: string) => {
        const date = new Date(dateString);
        return date.toLocaleString('ru-RU', {
            day: '2-digit', month: 'short', hour: '2-digit', minute: '2-digit'
        });
    };

    return (
        <div className="bg-white rounded-xl shadow-sm border border-gray-100 overflow-hidden hover:shadow-md transition-shadow duration-300">
            {/* Заглушка для картинки */}
            <div className="h-48 bg-gray-200 flex items-center justify-center relative">
                <span className="text-gray-400">Нет фото</span>
                <div className="absolute top-2 right-2 bg-green-500 text-white text-xs font-bold px-2 py-1 rounded">
                    Активен
                </div>
            </div>

            <div className="p-5">
                <h3 className="font-bold text-lg text-gray-800 mb-2 truncate" title={lot.title}>
                    {lot.title}
                </h3>

                <div className="flex justify-between items-end mb-4">
                    <div>
                        <p className="text-sm text-gray-500">Текущая ставка</p>
                        <p className="text-2xl font-bold text-blue-600">${lot.currentPrice.toFixed(2)}</p>
                    </div>
                    <div className="text-right">
                        <p className="text-xs text-gray-500 mb-1">До окончания</p>
                        <p className="text-sm font-medium text-gray-700">{formatEndTime(lot.endTime)}</p>
                    </div>
                </div>

                <Link
                    to={`/lots/${lot.id}`}
                    className="block w-full text-center bg-gray-900 hover:bg-gray-800 text-white font-medium py-2 rounded transition-colors"
                >
                    Сделать ставку
                </Link>
            </div>
        </div>
    );
};
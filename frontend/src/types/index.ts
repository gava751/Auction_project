export interface Lot {
    id: number;
    title: string;
    currentPrice: number;
    endTime: string;
    status: string;
    eurPrice?: number;
}

export interface PageResponse<T> {
    content: T[];
    totalPages: number;
    totalElements: number;
    size: number;
    number: number;
}
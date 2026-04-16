"use client";

import React from "react";
import { Filter } from "lucide-react";
import { StatCards } from "@/features/dashboard/components/StatCards";
import { AlertBanner } from "@/features/dashboard/components/AlertBanner";
import { RecentPosts } from "@/features/dashboard/components/RecentPosts";
import { RecentActivity } from "@/features/dashboard/components/RecentActivity";


export default function DashboardPage() {
    return (
        <div className="flex flex-col gap-10 p-6 md:px-10 md:py-10 w-full max-w-[1400px] mx-auto">
            {/* Header */}
            <div className="flex flex-col justify-between gap-4 md:flex-row md:items-center">
                <div>
                    <h1 className="text-[28px] font-bold text-slate-800 font-outfit tracking-tight">Accueil</h1>
                </div>
                <div className="flex items-center gap-3">
                    <button className="flex items-center gap-2 rounded-xl bg-white border border-gray-200 px-4 py-2.5 text-sm font-bold text-slate-700 shadow-sm transition-colors hover:bg-gray-50">
                        <Filter className="h-4 w-4" />
                        Filtrer
                    </button>
                    <button className="flex items-center gap-2 rounded-xl bg-[#7B51FA] px-4 py-2.5 text-sm font-bold text-white shadow-[0_4px_14px_rgba(123,81,250,0.39)] transition-all hover:bg-[#6A41E8] hover:shadow-[0_6px_20px_rgba(123,81,250,0.23)] active:scale-[0.98]">
                        Créer un nouveau post
                    </button>
                </div>
            </div>

            {/* Alerts */}
            <AlertBanner />

            {/* Stats */}
            <div>
                <StatCards />
            </div>

            {/* Advanced KPIs */}


            {/* Grid: 2 Columns */}
            <div className="grid gap-6 lg:grid-cols-[1.5fr_1fr]">
                <div className="flex flex-col gap-5">

                    <RecentPosts />
                </div>
                <div className="flex flex-col gap-5">

                    <RecentActivity />
                </div>
            </div>
        </div>
    );
}

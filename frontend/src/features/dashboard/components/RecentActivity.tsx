"use client";

import React from "react";

const activities = [
    {
        id: 1,
        title: "Post publié sur Facebook et LinkedIn",
        time: "Il y a 24h",
        statusColor: "bg-emerald-500",
    },
    {
        id: 2,
        title: "Post planifié pour dans 6h sur LinkedIn",
        time: "Il y a 2h",
        statusColor: "bg-[#7B51FA]",
    },
    {
        id: 3,
        title: "Échec — Token Facebook expiré",
        time: "Il y a 48h",
        statusColor: "bg-rose-500",
    },
    {
        id: 4,
        title: "Compte LinkedIn connecté",
        time: "Il y a 3 jours",
        statusColor: "bg-[#7B51FA]",
    },
];

export const RecentActivity = () => {
    return (
        <div className="flex h-full flex-col overflow-hidden rounded-2xl border border-gray-100 bg-white shadow-[0_2px_10px_rgba(0,0,0,0.02)]">
            {/* Header */}
            <div className="border-b border-gray-100 px-6 py-5">
                <h3 className="font-bold text-slate-800 font-outfit text-lg">Activité récente</h3>
            </div>

            {/* Timeline List */}
            <div className="flex-1 p-6">
                <div className="relative space-y-6">
                    {/* Vertical line connector */}
                    <div className="absolute bottom-0 left-[11px] top-2 w-px bg-gray-100" />

                    {activities.map((activity) => (
                        <div key={activity.id} className="relative flex gap-4">
                            {/* Dot */}
                            <div className="relative z-10 flex h-6 w-6 shrink-0 items-center justify-center rounded-full bg-white ring-4 ring-white">
                                <div className={`h-3 w-3 rounded-full ${activity.statusColor} shadow-sm`} />
                            </div>

                            {/* Content */}
                            <div className="flex flex-col gap-1 pb-1 pt-0.5">
                                <p className="text-sm font-bold text-slate-800">{activity.title}</p>
                                <p className="text-xs font-medium text-slate-500">{activity.time}</p>
                            </div>
                        </div>
                    ))}
                </div>
            </div>
        </div>
    );
};

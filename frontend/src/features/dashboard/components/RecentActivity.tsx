"use client";

import React, { useState } from "react";
import { MoreVertical } from "lucide-react";

export const RecentActivity = () => {
    const [period, setPeriod] = useState("Monthly");

    const stats = [
        {
            title: "Traffic Stats",
            // We use this array for mapping, adjusting title logic
        }
    ]; // Not strictly needed this way. Let's declare the items properly.

    const metrics = [
        {
            title: "New Subscribers",
            value: "567K",
            trend: "+3.85%",
            context: "then last Week",
            isPositive: true,
            points: "0,25 15,22 30,28 45,20 60,25 75,20 90,26 100,15"
        },
        {
            title: "Conversion Rate",
            value: "276K",
            trend: "-5.39%",
            context: "then last Week",
            isPositive: false,
            points: "0,15 15,20 30,12 45,28 60,22 75,30 90,24 100,35"
        },
        {
            title: "Page Bounce Rate",
            value: "285",
            trend: "+12.74%",
            context: "then last Week",
            isPositive: true,
            points: "0,35 15,28 30,32 45,20 60,26 75,18 90,24 100,10"
        }
    ];

    return (
        <div className="flex w-full flex-col overflow-hidden rounded-[20px] border border-slate-200 bg-white shadow-[0px_4px_16px_rgba(17,17,26,0.02),0px_1px_4px_rgba(17,17,26,0.03)] p-6 md:p-8">

            {/* Header */}
            <div className="flex items-center justify-between mb-6">
                <h3 className="text-xl font-extrabold text-slate-800 tracking-tight">Traffic Stats</h3>
                <button className="text-slate-400 hover:text-slate-600 transition-colors">
                    <MoreVertical className="h-5 w-5" />
                </button>
            </div>

            {/* Segmented Control */}
            <div className="flex items-center p-1.5 bg-[#F4F5F7] rounded-xl mb-8">
                {['Monthly', 'Quarterly', 'Annually'].map(p => (
                    <button
                        key={p}
                        onClick={() => setPeriod(p)}
                        className={`flex-1 py-2 text-[14px] font-bold rounded-lg transition-all ${period === p
                            ? 'bg-white text-slate-800 shadow-[0_1px_3px_rgba(0,0,0,0.05)]'
                            : 'text-slate-500 hover:text-slate-700'
                            }`}
                    >
                        {p}
                    </button>
                ))}
            </div>

            {/* Metrics List */}
            <div className="flex flex-col">
                {metrics.map((stat, i) => (
                    <div
                        key={stat.title}
                        className={`py-5 flex items-center justify-between ${i !== metrics.length - 1 ? 'border-b border-gray-100' : ''
                            }`}
                    >
                        <div className="flex flex-col gap-1.5">
                            <span className="text-[14px] font-semibold text-slate-500">{stat.title}</span>
                            <span className="text-[28px] font-extrabold text-slate-800 tracking-tight leading-none">{stat.value}</span>
                            <div className="flex items-center gap-1.5 mt-1.5">
                                <span className={`text-[12px] font-bold ${stat.isPositive ? 'text-emerald-500' : 'text-rose-500'}`}>
                                    {stat.trend}
                                </span>
                                <span className="text-[12px] font-semibold text-slate-400">{stat.context}</span>
                            </div>
                        </div>

                        {/* Sparkline */}
                        <div className="w-[100px] h-[50px] shrink-0">
                            <svg width="100%" height="100%" viewBox="0 0 100 40" preserveAspectRatio="none">
                                <defs>
                                    <linearGradient id={`gradient-green-${i}`} x1="0" x2="0" y1="0" y2="1">
                                        <stop offset="0%" stopColor="#10B981" stopOpacity="0.25" />
                                        <stop offset="100%" stopColor="#10B981" stopOpacity="0" />
                                    </linearGradient>
                                    <linearGradient id={`gradient-red-${i}`} x1="0" x2="0" y1="0" y2="1">
                                        <stop offset="0%" stopColor="#F43F5E" stopOpacity="0.25" />
                                        <stop offset="100%" stopColor="#F43F5E" stopOpacity="0" />
                                    </linearGradient>
                                </defs>
                                <polyline
                                    points={stat.points}
                                    fill="none"
                                    stroke={stat.isPositive ? "#10B981" : "#F43F5E"}
                                    strokeWidth="2"
                                    strokeLinejoin="round"
                                />
                                <polygon
                                    points={`${stat.points} 100,40 0,40`}
                                    fill={`url(#gradient-${stat.isPositive ? 'green' : 'red'}-${i})`}
                                />
                            </svg>
                        </div>
                    </div>
                ))}
            </div>
        </div>
    );
};

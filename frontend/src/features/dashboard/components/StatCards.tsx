"use client";

import React from "react";

export const StatCards = () => {
    const stats = [
        {
            title: "Unique Visitors",
            value: "24.7K",
            trend: "+20%",
            context: "Vs last month",
            isPositive: true,
        },
        {
            title: "Total Pageviews",
            value: "55.9K",
            trend: "+4%",
            context: "Vs last month",
            isPositive: true,
        },
        {
            title: "Bounce Rate",
            value: "54%",
            trend: "-1.59%",
            context: "Vs last month",
            isPositive: false,
        },
        {
            title: "Visit Duration",
            value: "2m 56s",
            trend: "+7%",
            context: "Vs last month",
            isPositive: true,
        },
    ];

    return (
        <div className="grid gap-6 md:grid-cols-2 lg:grid-cols-4">
            {stats.map((stat) => (
                <div
                    key={stat.title}
                    className="flex flex-col justify-between rounded-[14px] border border-slate-100 bg-white p-6 shadow-[0px_4px_16px_rgba(17,17,26,0.02),0px_1px_4px_rgba(17,17,26,0.03)]"
                >
                    <p className="text-[13px] font-semibold text-slate-500 mb-6">{stat.title}</p>

                    <div className="flex items-end justify-between">
                        <h2 className="text-[26px] font-extrabold tracking-tight text-slate-800">
                            {stat.value}
                        </h2>

                        <div className="flex items-center gap-2 mb-1">
                            <span
                                className={`text-[11px] font-bold px-2 py-0.5 rounded-full ${stat.isPositive
                                    ? "bg-emerald-50 text-emerald-600"
                                    : "bg-rose-50 text-rose-500"
                                    }`}
                            >
                                {stat.trend}
                            </span>
                            <span className="text-[11px] font-medium text-slate-400">
                                {stat.context}
                            </span>
                        </div>
                    </div>
                </div>
            ))}
        </div>
    );
};

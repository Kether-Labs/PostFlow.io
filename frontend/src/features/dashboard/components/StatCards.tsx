"use client";

import React from "react";
import { motion } from "framer-motion";
import { ArrowUpRight } from "lucide-react";

export const StatCards = () => {
    const stats = [
        {
            title: "Publiés cette semaine",
            value: "12",
            change: "+3 vs semaine dernière",
            isPositive: true,
        },
        {
            title: "Posts planifiés",
            value: "8",
            change: "Prochaines publications",
            isPositive: null,
        },
        {
            title: "Réseaux connectés",
            value: "2",
            change: "Facebook • LinkedIn",
            isPositive: null,
        },
        {
            title: "Crédits IA restants",
            value: "77",
            change: "23 / 100 utilisés",
            isPositive: null,
        },
    ];

    return (
        <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-4">
            {stats.map((stat, i) => (
                <motion.div
                    key={stat.title}
                    initial={{ opacity: 0, y: 20 }}
                    animate={{ opacity: 1, y: 0 }}
                    transition={{ duration: 0.4, delay: i * 0.1 }}
                    className="flex flex-col gap-3 rounded-2xl border border-gray-100 bg-white p-5 shadow-[0_2px_10px_rgba(0,0,0,0.02)] transition-all hover:shadow-[0_4px_20px_rgba(0,0,0,0.04)]"
                >
                    <p className="text-sm font-semibold text-slate-500">{stat.title}</p>
                    <div className="flex items-baseline gap-2">
                        <h2 className="text-4xl font-bold tracking-tight text-slate-800 font-outfit">
                            {stat.value}
                        </h2>
                    </div>
                    <div className="mt-auto">
                        <span className={`text-xs font-bold flex items-center gap-1 ${stat.isPositive === true ? "text-emerald-500" :
                                stat.isPositive === false ? "text-rose-500" :
                                    "text-slate-400"
                            }`}>
                            {stat.isPositive === true && <ArrowUpRight className="h-3.5 w-3.5" />}
                            {stat.change}
                        </span>
                    </div>
                </motion.div>
            ))}
        </div>
    );
};

"use client";

import React from "react";
import Link from "next/link";
import { ArrowRight } from "lucide-react";

const posts = [
    {
        id: 1,
        title: "🚀 PostFlow.io est maintenant dis...",
        networks: "FACEBOOK • LINKEDIN",
        time: "Publié il y a 24h",
        status: "Publié",
        statusColor: "bg-emerald-500",
        badgeColor: "bg-emerald-50 text-emerald-600",
    },
    {
        id: 2,
        title: "5 erreurs que font les créateurs d...",
        networks: "LINKEDIN",
        time: "Dans 6h",
        status: "Planifié",
        statusColor: "bg-[#7B51FA]",
        badgeColor: "bg-[#F3EFFF] text-[#7B51FA]",
    },
    {
        id: 3,
        title: "Notre nouvelle fonctionnalité IA...",
        networks: "Aucun réseau",
        time: "Brouillon",
        status: "Brouillon",
        statusColor: "bg-slate-400",
        badgeColor: "bg-slate-100 text-slate-600",
    },
    {
        id: 4,
        title: "Bonne semaine à tous ! La régulari...",
        networks: "FACEBOOK",
        time: "Token expiré",
        status: "Échec",
        statusColor: "bg-rose-500",
        badgeColor: "bg-rose-50 text-rose-600",
    },
];

export const RecentPosts = () => {
    return (
        <div className="flex flex-col overflow-hidden rounded-2xl border border-gray-100 bg-white shadow-[0_2px_10px_rgba(0,0,0,0.02)]">
            {/* Header */}
            <div className="flex items-center justify-between border-b border-gray-100 px-6 py-5">
                <h3 className="font-bold text-slate-800 font-outfit text-lg">Posts récents</h3>
                <Link
                    href="/dashboard/posts"
                    className="flex items-center gap-1 text-sm font-bold text-[#7B51FA] hover:text-[#6A41E8] transition-colors"
                >
                    Voir tout <ArrowRight className="h-4 w-4" />
                </Link>
            </div>

            {/* List */}
            <div className="divide-y divide-gray-50">
                {posts.map((post) => (
                    <div
                        key={post.id}
                        className="flex items-center justify-between gap-4 px-6 py-4 transition-colors hover:bg-gray-50 cursor-pointer"
                    >
                        <div className="flex items-start gap-4 overflow-hidden">
                            <div className={`mt-2 h-2.5 w-2.5 shrink-0 rounded-full ${post.statusColor}`} />
                            <div className="min-w-0">
                                <p className="truncate font-bold text-slate-800">{post.title}</p>
                                <div className="mt-1 flex items-center gap-2 text-xs text-slate-500 font-medium">
                                    <span className="font-bold">{post.networks}</span>
                                    <span>•</span>
                                    <span>{post.time}</span>
                                </div>
                            </div>
                        </div>
                        <div className={`shrink-0 rounded-lg px-2.5 py-1 text-xs font-bold ${post.badgeColor}`}>
                            {post.status}
                        </div>
                    </div>
                ))}
            </div>
        </div>
    );
};

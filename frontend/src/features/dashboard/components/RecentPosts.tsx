"use client";

import React from "react";
import Link from "next/link";
import { Settings2 } from "lucide-react";

const posts = [
    {
        id: 1,
        title: "Lancement de PostFlow.io 🚀",
        variants: "2 Variantes",
        networks: "Facebook, LinkedIn",
        time: "Aujourd'hui à 10:00",
        status: "Publié",
        statusClass: "bg-emerald-50 text-emerald-600",
        image: "https://images.unsplash.com/photo-1611162617474-5b21e879e113?w=100&h=100&fit=crop",
    },
    {
        id: 2,
        title: "Conseils pour les créateurs",
        variants: "1 Variante",
        networks: "LinkedIn",
        time: "Demain à 14:00",
        status: "Planifié",
        statusClass: "bg-orange-50 text-orange-500",
        image: "https://images.unsplash.com/photo-1616469829581-73993eb86b02?w=100&h=100&fit=crop",
    },
    {
        id: 3,
        title: "Notre nouvelle fonctionnalité",
        variants: "3 Variantes",
        networks: "Facebook, Twitter",
        time: "Hier à 18:30",
        status: "Publié",
        statusClass: "bg-emerald-50 text-emerald-600",
        image: "https://images.unsplash.com/photo-1622228514167-938171d182ea?w=100&h=100&fit=crop",
    },
    {
        id: 4,
        title: "Erreur de connexion API",
        variants: "1 Variante",
        networks: "Facebook",
        time: "Il y a 2 jours",
        status: "Échec",
        statusClass: "bg-rose-50 text-rose-500",
        image: "https://images.unsplash.com/photo-1594322436404-5a0526db4d13?w=100&h=100&fit=crop",
    },
    {
        id: 5,
        title: "Mise à jour des CGU",
        variants: "1 Variante",
        networks: "Twitter",
        time: "Il y a 3 jours",
        status: "Publié",
        statusClass: "bg-emerald-50 text-emerald-600",
        image: "https://images.unsplash.com/photo-1541802052199-651c68e1aade?w=100&h=100&fit=crop",
    }
];

export const RecentPosts = () => {
    return (
        <div className="flex flex-col overflow-hidden rounded-2xl border border-slate-100 bg-white shadow-[0px_4px_16px_rgba(17,17,26,0.02),0px_1px_4px_rgba(17,17,26,0.03)]">

            {/* Header */}
            <div className="flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4 px-7 py-6">
                <h3 className="text-xl font-extrabold text-slate-800 tracking-tight">Activité Récente</h3>
                <div className="flex items-center gap-3">
                    <button className="flex items-center gap-2 rounded-lg border border-slate-200 px-4 py-2 text-sm font-bold text-slate-700 hover:bg-slate-50 transition-colors shadow-sm">
                        <Settings2 className="h-4 w-4 text-slate-500" />
                        Filtrer
                    </button>
                    <Link
                        href="/dashboard/posts"
                        className="flex items-center gap-2 rounded-lg border border-slate-200 px-4 py-2 text-sm font-bold text-slate-700 hover:bg-slate-50 transition-colors shadow-sm whitespace-nowrap"
                    >
                        Voir tout
                    </Link>
                </div>
            </div>

            {/* Table */}
            <div className="w-full overflow-x-auto">
                <table className="w-full text-left border-collapse min-w-[700px]">
                    <thead>
                        <tr className="border-y border-slate-100">
                            <th className="py-4 px-7 text-[13px] font-bold text-slate-500 w-[40%]">Posts</th>
                            <th className="py-4 px-4 text-[13px] font-bold text-slate-500">Réseaux</th>
                            <th className="py-4 px-4 text-[13px] font-bold text-slate-500">Date prévue</th>
                            <th className="py-4 px-7 text-[13px] font-bold text-slate-500 text-right">Status</th>
                        </tr>
                    </thead>
                    <tbody className="divide-y divide-slate-100">
                        {posts.map((post) => (
                            <tr key={post.id} className="transition-colors hover:bg-slate-50/50">
                                <td className="py-5 px-7">
                                    <div className="flex items-center gap-4">
                                        <div className="h-12 w-16 shrink-0 overflow-hidden rounded-md bg-slate-100 border border-slate-200">
                                            <img src={post.image} alt={post.title} className="h-full w-full object-cover" />
                                        </div>
                                        <div className="flex flex-col gap-1 min-w-0">
                                            <p className="truncate font-bold text-slate-800 text-sm">{post.title}</p>
                                            <p className="text-xs font-semibold text-slate-500">{post.variants}</p>
                                        </div>
                                    </div>
                                </td>
                                <td className="py-5 px-4 text-sm font-semibold text-slate-500">
                                    {post.networks}
                                </td>
                                <td className="py-5 px-4 text-sm font-semibold text-slate-500">
                                    {post.time}
                                </td>
                                <td className="py-5 px-7 text-right">
                                    <span
                                        className={`inline-flex rounded-full px-3 py-1 text-[11px] font-bold ${post.statusClass}`}
                                    >
                                        {post.status}
                                    </span>
                                </td>
                            </tr>
                        ))}
                    </tbody>
                </table>
            </div>

        </div>
    );
};

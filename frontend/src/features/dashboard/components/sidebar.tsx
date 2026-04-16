"use client";

import React, { useState } from "react";
import Link from "next/link";
import { usePathname } from "next/navigation";
import { motion, AnimatePresence } from "framer-motion";
import {
    Home,
    FileText,
    Calendar,
    PlusCircle,
    Share2,
    BarChart2,
    Settings,
    CreditCard,
    ChevronRight,
    Search,
    Plus,
    CheckCircle2
} from "lucide-react";
import { AddNetworkModal } from "./AddNetworkModal";

const sections = [
    {
        title: "Général",
        links: [
            { name: "Accueil", href: "/dashboard", icon: Home }
        ]
    },
    {
        title: "Contenu",
        links: [
            { name: "Mes posts", href: "/dashboard/posts", icon: FileText, badge: "8", badgeColor: "bg-[#7B51FA]/10 text-[#7B51FA]" },
            { name: "Calendrier", href: "/dashboard/calendar", icon: Calendar },
            { name: "Nouveau post", href: "/dashboard/new", icon: PlusCircle }
        ]
    },
    {
        title: "Réseaux",
        links: [
            { name: "Réseaux sociaux", href: "/dashboard/networks", icon: Share2, badge: "2", badgeColor: "bg-orange-100 text-orange-600" }
        ]
    },
    {
        title: "Analyse",
        links: [
            { name: "Analytics", href: "/dashboard/analytics", icon: BarChart2, badge: "Bientôt", badgeColor: "bg-slate-100 text-slate-500" }
        ]
    },
    {
        title: "Compte",
        links: [
            { name: "Réglages", href: "/dashboard/settings", icon: Settings },
            { name: "Abonnement", href: "/dashboard/subscription", icon: CreditCard, badge: "Free", badgeColor: "bg-slate-100 text-slate-800" }
        ]
    }
];

export const DashboardSidebar = () => {
    const pathname = usePathname();
    const [isWorkspaceOpen, setIsWorkspaceOpen] = useState(false);
    const [isNetworkModalOpen, setIsNetworkModalOpen] = useState(false);

    return (
        <>
            <aside className="h-full z-40 flex w-[280px] flex-col bg-white rounded-[24px] text-slate-800 transition-all duration-300 md:relative md:translate-x-0 -translate-x-full shadow-[0_2px_20px_rgba(0,0,0,0.02)] border border-gray-100 overflow-hidden">

                {/* Brand & Logo */}
                <div className="flex h-16 items-center px-6 pt-4 mb-2">
                    <Link href="/dashboard" className="flex items-center gap-2">
                        <div className="flex items-center justify-center rounded-lg bg-[#7B51FA] w-8 h-8 text-white font-bold text-lg shadow-[0_4px_10px_rgba(123,81,250,0.3)]">
                            P
                        </div>
                        <span className="text-xl font-black font-outfit tracking-tight text-slate-900">
                            Post<span className="text-[#7B51FA]">Flow</span>.io
                        </span>
                    </Link>
                </div>

                {/* Workspace Switcher */}
                <div className="px-5 relative mb-6 z-50">
                    <button
                        onClick={() => setIsWorkspaceOpen(!isWorkspaceOpen)}
                        className={`flex w-full items-center justify-between rounded-xl border px-4 py-3 transition-all duration-200 ${isWorkspaceOpen
                            ? 'bg-[#F4F5F7] border-[#7B51FA]/30 shadow-inner'
                            : 'border-gray-100 bg-white shadow-[0_2px_10px_rgba(0,0,0,0.03)] hover:shadow-[0_2px_15px_rgba(0,0,0,0.06)] hover:border-gray-200'
                            }`}
                    >
                        <div className="flex flex-col items-start translate-y-[1px]">
                            <span className="font-bold text-slate-800 font-outfit text-[15px] leading-none">Team Dev</span>
                            <span className="text-[10px] font-bold text-[#7B51FA] mt-1 tracking-wider uppercase">Workspace</span>
                        </div>
                        <div className="flex items-center gap-2">
                            <span className="flex items-center justify-center rounded-full bg-[#7B51FA] text-white px-2 py-0.5 text-[10px] font-bold shadow-[0_2px_5px_rgba(123,81,250,0.4)]">
                                95
                            </span>
                            <motion.div
                                animate={{ rotate: isWorkspaceOpen ? 90 : 0 }}
                                className="flex items-center justify-center w-5 h-5 rounded-full border border-gray-200"
                            >
                                <ChevronRight className="h-3 w-3 text-gray-400" />
                            </motion.div>
                        </div>
                    </button>

                    {/* Workspace Dropdown */}
                    <AnimatePresence>
                        {isWorkspaceOpen && (
                            <motion.div
                                initial={{ opacity: 0, y: -10, scale: 0.95 }}
                                animate={{ opacity: 1, y: 0, scale: 1 }}
                                exit={{ opacity: 0, y: -10, scale: 0.95 }}
                                transition={{ duration: 0.2, ease: "easeOut" }}
                                className="absolute left-5 right-5 top-[calc(100%+8px)] rounded-xl border border-gray-100 bg-white p-2 shadow-2xl z-[60] origin-top"
                            >
                                <div className="relative mb-2">
                                    <Search className="absolute left-3 top-1/2 -translate-y-1/2 h-4 w-4 text-gray-400" />
                                    <input
                                        type="text"
                                        placeholder="Rechercher des workspaces..."
                                        className="w-full rounded-lg bg-gray-50 py-2.5 pl-9 pr-3 text-sm text-slate-700 outline-none placeholder:text-gray-400 focus:bg-gray-100 transition-colors border border-transparent focus:border-gray-200"
                                    />
                                </div>

                                <div className="flex items-center justify-between rounded-lg bg-gray-50/80 px-3 py-2.5 cursor-pointer hover:bg-gray-100 transition-colors">
                                    <div className="flex items-center gap-2">
                                        <div className="w-8 h-8 rounded bg-gradient-to-br from-[#7B51FA] to-[#6A41E8] flex items-center justify-center text-white text-xs font-bold">TD</div>
                                        <div className="flex flex-col">
                                            <span className="font-bold text-slate-800 text-sm">Team Dev</span>
                                            <span className="text-[10px] text-gray-400">95 utilisateurs</span>
                                        </div>
                                    </div>
                                    <CheckCircle2 className="h-4 w-4 text-[#7B51FA]" />
                                </div>

                                <div className="mt-2 text-center text-xs">
                                    <button className="flex w-full items-center justify-center gap-2 rounded-lg border border-gray-200 border-dashed py-2.5 font-bold text-slate-500 hover:bg-gray-50 hover:text-[#7B51FA] hover:border-[#7B51FA]/30 transition-all">
                                        <Plus className="h-3.5 w-3.5" />
                                        Nouveau workspace
                                    </button>
                                </div>
                            </motion.div>
                        )}
                    </AnimatePresence>
                </div>

                {/* Navigation Links Grouped */}
                <div className="flex-1 overflow-y-auto px-4 space-y-3 scrollbar-hide py-2">
                    {sections.map((section) => (
                        <div key={section.title} className="space-y-1">
                            <h4 className="px-3 text-[11px] font-bold text-slate-400 uppercase tracking-[0.1em] mb-3">
                                {section.title}
                            </h4>
                            <div className="space-y-[2px]">
                                {section.links.map((link) => {
                                    const isActive = pathname === link.href || (link.href !== "/dashboard" && pathname.startsWith(link.href));
                                    const Icon = link.icon;

                                    return (
                                        <Link
                                            key={link.name}
                                            href={link.href}
                                            className={`group flex items-center justify-between rounded-xl px-3 py-2.5 text-sm font-bold transition-all duration-200 ${isActive
                                                ? "bg-[#F3EFFF] text-[#7B51FA]"
                                                : "text-slate-600 hover:bg-gray-50 hover:text-slate-900"
                                                }`}
                                        >
                                            <div className="flex items-center gap-3">
                                                <div className={`flex items-center justify-center w-8 h-8 rounded-lg transition-colors ${isActive ? "bg-white shadow-sm" : "group-hover:bg-white group-hover:shadow-sm"}`}>
                                                    <Icon className={`h-[18px] w-[18px] ${isActive ? "text-[#7B51FA]" : "text-slate-400 group-hover:text-slate-600"}`} />
                                                </div>
                                                <span className="translate-y-[1px]">{link.name}</span>
                                            </div>
                                            {link.badge && (
                                                <span className={`flex h-5 items-center justify-center rounded-full px-2 min-w-[20px] text-[10px] font-black shadow-sm ${link.badgeColor || "bg-gray-300 text-white"}`}>
                                                    {link.badge}
                                                </span>
                                            )}
                                        </Link>
                                    );
                                })}
                            </div>
                        </div>
                    ))}
                </div>

                {/* User Profile & Network CTA */}
                <div className="p-4 bg-white border-t border-gray-50 z-10">
                    <div className="mb-5 flex items-center justify-between px-2">
                        <div className="flex items-center gap-3">
                            <div className="relative">
                                <div className="h-10 w-10 overflow-hidden rounded-full border-2 border-[#7B51FA]/10 bg-gray-50">
                                    <img src="https://api.dicebear.com/7.x/avataaars/svg?seed=Martin" alt="User" className="h-full w-full bg-gray-100" />
                                </div>
                                <div className="absolute -bottom-0.5 -right-0.5 flex h-4 w-4 items-center justify-center rounded-full bg-white shadow-sm border border-gray-100">
                                    <div className="flex h-3 w-3 items-center justify-center rounded-full bg-[#7B51FA] text-white text-[8px] font-bold">Pf</div>
                                </div>
                            </div>
                            <div className="flex flex-col">
                                <span className="text-[14px] font-bold text-slate-800 leading-none">Jean Dupont</span>
                                <span className="text-[11px] font-semibold text-slate-400 mt-1">jean@postflow.io</span>
                            </div>
                        </div>
                        <span className="flex items-center justify-center rounded-lg bg-orange-100 text-orange-600 px-2 py-1 text-[9px] font-black uppercase tracking-wider shadow-sm">
                            Free
                        </span>
                    </div>

                    <button
                        onClick={() => setIsNetworkModalOpen(true)}
                        className="flex w-full items-center justify-center gap-2.5 rounded-xl bg-[#7B51FA] py-3.5 text-[14px] font-bold text-white shadow-[0_4px_14px_rgba(123,81,250,0.3)] transition-all hover:bg-[#6A41E8] hover:shadow-[0_6px_20px_rgba(123,81,250,0.4)] active:scale-[0.98] group"
                    >
                        <div className="flex h-5 w-5 items-center justify-center rounded-lg bg-white/20 text-white transition-transform group-hover:rotate-90">
                            <Plus className="h-3.5 w-3.5" />
                        </div>
                        Ajouter un réseau
                    </button>
                </div>
            </aside>

            <AddNetworkModal
                isOpen={isNetworkModalOpen}
                onClose={() => setIsNetworkModalOpen(false)}
            />
        </>
    );
};

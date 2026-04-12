"use client";

import React, { useState } from "react";
import Link from "next/link";
import { usePathname } from "next/navigation";
import { motion, AnimatePresence } from "framer-motion";
import {
    Home,
    MessageCircle,
    Inbox,
    Settings,
    Users,
    Zap,
    ChevronRight,
    Search,
    Plus,
    CheckCircle2
} from "lucide-react";
import { AddNetworkModal } from "./AddNetworkModal";

const links = [
    { name: "Accueil", href: "/dashboard", icon: Home },
    { name: "Commentaires", href: "/dashboard/comments", icon: MessageCircle, badge: 77 },
    { name: "Messages", href: "/dashboard/messages", icon: Inbox, badge: 18 },
    { name: "Automatisations", href: "/dashboard/automations", icon: Zap },
    { name: "Contacts", href: "/dashboard/contacts", icon: Users },
    { name: "Paramètres", href: "/dashboard/settings", icon: Settings }
];

interface AddNetworkModalProps {
    isOpen: boolean;
    onClose: () => void;
}

// We will build AddNetworkModal in a separate file, but using a prop to control it here.


export const DashboardSidebar = () => {
    const pathname = usePathname();
    const [isWorkspaceOpen, setIsWorkspaceOpen] = useState(false);
    const [isNetworkModalOpen, setIsNetworkModalOpen] = useState(false);

    return (
        <>
            <aside className="h-full z-40 flex w-[280px] flex-col bg-white rounded-[24px] text-slate-800 transition-transform duration-300 md:relative md:translate-x-0 -translate-x-full shadow-[0_2px_20px_rgba(0,0,0,0.02)] border border-gray-100 overflow-hidden">

                {/* Brand & Logo */}
                <div className="flex h-16 items-center px-6 pt-4 mb-2">
                    <Link href="/dashboard" className="flex items-center gap-2">
                        <div className="flex items-center justify-center rounded-lg bg-[#7B51FA] w-8 h-8 text-white font-bold text-lg">
                            P
                        </div>
                        <span className="text-xl font-black font-outfit tracking-tight text-slate-900">
                            Post<span className="text-slate-500">Flow</span>
                        </span>
                    </Link>
                </div>

                {/* Workspace Switcher */}
                <div className="px-5 relative mb-6 z-50">
                    <button
                        onClick={() => setIsWorkspaceOpen(!isWorkspaceOpen)}
                        className="flex w-full items-center justify-between rounded-xl border border-gray-100 bg-white px-4 py-3 shadow-[0_2px_10px_rgba(0,0,0,0.03)] hover:shadow-[0_2px_15px_rgba(0,0,0,0.06)] transition-all"
                    >
                        <span className="font-bold text-slate-800 font-outfit text-base">Team Dev</span>
                        <div className="flex items-center gap-2">
                            <span className="flex items-center justify-center rounded-full bg-[#7B51FA] text-white px-2 py-0.5 text-[10px] font-bold">
                                95
                            </span>
                            <div className="flex items-center justify-center w-5 h-5 rounded-full border border-gray-200">
                                <ChevronRight className="h-3 w-3 text-gray-400" />
                            </div>
                        </div>
                    </button>

                    {/* Workspace Dropdown */}
                    <AnimatePresence>
                        {isWorkspaceOpen && (
                            <motion.div
                                initial={{ opacity: 0, y: -10 }}
                                animate={{ opacity: 1, y: 0 }}
                                exit={{ opacity: 0, y: -10 }}
                                transition={{ duration: 0.15 }}
                                className="absolute left-5 right-5 top-[calc(100%+8px)] rounded-xl border border-gray-100 bg-white p-2 shadow-xl"
                            >
                                <div className="relative mb-2">
                                    <Search className="absolute left-3 top-1/2 -translate-y-1/2 h-4 w-4 text-gray-400" />
                                    <input
                                        type="text"
                                        placeholder="Rechercher des workspaces..."
                                        className="w-full rounded-lg bg-gray-50 py-2 pl-9 pr-3 text-sm text-slate-700 outline-none placeholder:text-gray-400 focus:bg-gray-100 transition-colors"
                                    />
                                </div>

                                <div className="flex items-center justify-between rounded-lg bg-gray-50/80 px-3 py-2.5 cursor-pointer">
                                    <div className="flex items-center gap-2">
                                        <span className="font-bold text-slate-800 text-sm">Team Dev</span>
                                        <span className="flex items-center justify-center rounded-full bg-[#7B51FA] text-white px-1.5 py-0.5 text-[10px] font-bold">95</span>
                                    </div>
                                    <CheckCircle2 className="h-4 w-4 text-rose-500" />
                                </div>

                                <div className="mt-2 text-center text-xs">
                                    <button className="flex w-full items-center justify-center gap-2 rounded-lg border border-gray-200 border-dashed py-2 font-medium text-slate-600 hover:bg-gray-50 hover:text-slate-900 transition-colors">
                                        <Plus className="h-3.5 w-3.5" />
                                        Ajouter un nouveau workspace
                                    </button>
                                </div>
                            </motion.div>
                        )}
                    </AnimatePresence>
                </div>

                {/* Navigation Links */}
                <div className="flex-1 overflow-y-auto px-3 space-y-1 scrollbar-hide">
                    {links.map((link) => {
                        const isActive = pathname === link.href || (link.href !== "/dashboard" && pathname.startsWith(link.href));
                        const Icon = link.icon;

                        return (
                            <Link
                                key={link.name}
                                href={link.href}
                                className={`group flex items-center justify-between rounded-xl px-4 py-3 text-sm font-semibold transition-all ${isActive
                                    ? "bg-[#F3EFFF] text-[#1E1E1E]"
                                    : "text-slate-600 hover:bg-gray-50 hover:text-slate-900"
                                    }`}
                            >
                                <div className="flex items-center gap-4">
                                    <Icon className={`h-[18px] w-[18px] ${isActive ? "text-[#7B51FA]" : "text-gray-400 group-hover:text-slate-600"}`} />
                                    <span>{link.name}</span>
                                </div>
                                {link.badge && (
                                    <span className="flex h-5 items-center justify-center rounded-full bg-gray-300 text-white px-1.5 min-w-[20px] text-[10px] font-bold shadow-sm">
                                        {link.badge}
                                    </span>
                                )}
                            </Link>
                        );
                    })}
                </div>

                {/* User Profile & Network CTA */}
                <div className="p-4 bg-white z-10">
                    <div className="mb-4 flex items-center gap-3 px-2">
                        <div className="relative">
                            <div className="h-10 w-10 overflow-hidden rounded-full border border-gray-50">
                                <img src="https://api.dicebear.com/7.x/avataaars/svg?seed=Martin" alt="User" className="h-full w-full bg-gray-100" />
                            </div>
                            <div className="absolute -bottom-1 -right-1 flex h-4 w-4 items-center justify-center rounded-full bg-white">
                                <div className="flex h-3 w-3 items-center justify-center rounded-full bg-blue-500 text-white text-[8px] font-bold">f</div>
                            </div>
                        </div>
                        <div className="flex flex-col">
                            <span className="text-sm font-bold text-slate-800">Martin dev</span>
                            <span className="text-xs text-gray-400">Facebook</span>
                        </div>
                    </div>

                    <button
                        onClick={() => setIsNetworkModalOpen(true)}
                        className="flex w-full items-center justify-center gap-2 rounded-xl bg-[#7B51FA] py-3 text-sm font-semibold text-white shadow-[0_4px_14px_rgba(123,81,250,0.39)] transition-all hover:bg-[#6A41E8] hover:shadow-[0_6px_20px_rgba(123,81,250,0.23)] active:scale-[0.98]"
                    >
                        <div className="flex h-4 w-4 items-center justify-center rounded-full bg-white text-[#7B51FA]">
                            <Plus className="h-3 w-3" />
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

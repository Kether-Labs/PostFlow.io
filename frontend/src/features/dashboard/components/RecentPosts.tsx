'use client'

import React from 'react'
import Link from 'next/link'
import { Settings2 } from 'lucide-react'
import Image from 'next/image'

type PlatformConfig = {
    icon: React.ReactNode;
    color: string;
};
const platformIcons: Record<string, PlatformConfig> = {
    Facebook: {
        icon: (
            <svg
                className=""
                xmlns="http://www.w3.org/2000/svg"
                viewBox="0 0 128 128"
            >
                <rect
                    fill="#3d5a98"
                    x="4.83"
                    y="4.83"
                    width="118.35"
                    height="118.35"
                    rx="6.53"
                    ry="6.53"
                />
                <path
                    fill="#fff"
                    d="M86.48 123.17V77.34h15.38l2.3-17.86H86.48v-11.4c0-5.17 1.44-8.7 8.85-8.7h9.46v-16A126.56 126.56 0 0091 22.7c-13.62 0-23 8.3-23 23.61v13.17H52.62v17.86H68v45.83z"
                />
            </svg>
        ),
        color: 'bg-blue-600',
    },
    LinkedIn: {
        icon: (
            <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 128 128">
                <path
                    fill="#0076b2"
                    d="M116 3H12a8.91 8.91 0 00-9 8.8v104.42a8.91 8.91 0 009 8.78h104a8.93 8.93 0 009-8.81V11.77A8.93 8.93 0 00116 3z"
                />
                <path
                    fill="#fff"
                    d="M21.06 48.73h18.11V107H21.06zm9.06-29a10.5 10.5 0 11-10.5 10.49 10.5 10.5 0 0110.5-10.49M50.53 48.73h17.36v8h.24c2.42-4.58 8.32-9.41 17.13-9.41C103.6 47.28 107 59.35 107 75v32H88.89V78.65c0-6.75-.12-15.44-9.41-15.44s-10.87 7.36-10.87 15V107H50.53z"
                />
            </svg>
        ),
        color: 'bg-blue-700',
    },

    Instagram: {
        icon: (
            <svg
                viewBox="0 0 3364.7 3364.7"
                xmlns="http://www.w3.org/2000/svg"
                fill="#000000"
            >
                <g id="SVGRepo_bgCarrier" strokeWidth="0"></g>
                <g
                    id="SVGRepo_tracerCarrier"
                    strokeLinecap="round"
                    strokeLinejoin="round"
                ></g>
                <g id="SVGRepo_iconCarrier">
                    <defs>
                        <radialGradient
                            id="0"
                            cx="217.76"
                            cy="3290.99"
                            r="4271.92"
                            gradientUnits="userSpaceOnUse"
                        >
                            <stop offset=".09" stopColor="#fa8f21"></stop>
                            <stop offset=".78" stopColor="#d82d7e"></stop>
                        </radialGradient>
                        <radialGradient
                            id="1"
                            cx="2330.61"
                            cy="3182.95"
                            r="3759.33"
                            gradientUnits="userSpaceOnUse"
                        >
                            <stop offset=".64" stopColor="#8c3aaa" stopOpacity="0"></stop>
                            <stop offset="1" stopColor="#8c3aaa"></stop>
                        </radialGradient>
                    </defs>
                    <path
                        d="M853.2,3352.8c-200.1-9.1-308.8-42.4-381.1-70.6-95.8-37.3-164.1-81.7-236-153.5S119.7,2988.6,82.6,2892.8c-28.2-72.3-61.5-181-70.6-381.1C2,2295.4,0,2230.5,0,1682.5s2.2-612.8,11.9-829.3C21,653.1,54.5,544.6,82.5,472.1,119.8,376.3,164.3,308,236,236c71.8-71.8,140.1-116.4,236-153.5C544.3,54.3,653,21,853.1,11.9,1069.5,2,1134.5,0,1682.3,0c548,0,612.8,2.2,829.3,11.9,200.1,9.1,308.6,42.6,381.1,70.6,95.8,37.1,164.1,81.7,236,153.5s116.2,140.2,153.5,236c28.2,72.3,61.5,181,70.6,381.1,9.9,216.5,11.9,281.3,11.9,829.3,0,547.8-2,612.8-11.9,829.3-9.1,200.1-42.6,308.8-70.6,381.1-37.3,95.8-81.7,164.1-153.5,235.9s-140.2,116.2-236,153.5c-72.3,28.2-181,61.5-381.1,70.6-216.3,9.9-281.3,11.9-829.3,11.9-547.8,0-612.8-1.9-829.1-11.9"
                        fill="url(#0)"
                    ></path>
                    <path
                        d="M853.2,3352.8c-200.1-9.1-308.8-42.4-381.1-70.6-95.8-37.3-164.1-81.7-236-153.5S119.7,2988.6,82.6,2892.8c-28.2-72.3-61.5-181-70.6-381.1C2,2295.4,0,2230.5,0,1682.5s2.2-612.8,11.9-829.3C21,653.1,54.5,544.6,82.5,472.1,119.8,376.3,164.3,308,236,236c71.8-71.8,140.1-116.4,236-153.5C544.3,54.3,653,21,853.1,11.9,1069.5,2,1134.5,0,1682.3,0c548,0,612.8,2.2,829.3,11.9,200.1,9.1,308.6,42.6,381.1,70.6,95.8,37.1,164.1,81.7,236,153.5s116.2,140.2,153.5,236c28.2,72.3,61.5,181,70.6,381.1,9.9,216.5,11.9,281.3,11.9,829.3,0,547.8-2,612.8-11.9,829.3-9.1,200.1-42.6,308.8-70.6,381.1-37.3,95.8-81.7,164.1-153.5,235.9s-140.2,116.2-236,153.5c-72.3,28.2-181,61.5-381.1,70.6-216.3,9.9-281.3,11.9-829.3,11.9-547.8,0-612.8-1.9-829.1-11.9"
                        fill="url(#1)"
                    ></path>
                    <path
                        d="M1269.25,1689.52c0-230.11,186.49-416.7,416.6-416.7s416.7,186.59,416.7,416.7-186.59,416.7-416.7,416.7-416.6-186.59-416.6-416.7m-225.26,0c0,354.5,287.36,641.86,641.86,641.86s641.86-287.36,641.86-641.86-287.36-641.86-641.86-641.86S1044,1335,1044,1689.52m1159.13-667.31a150,150,0,1,0,150.06-149.94h-0.06a150.07,150.07,0,0,0-150,149.94M1180.85,2707c-121.87-5.55-188.11-25.85-232.13-43-58.36-22.72-100-49.78-143.78-93.5s-70.88-85.32-93.5-143.68c-17.16-44-37.46-110.26-43-232.13-6.06-131.76-7.27-171.34-7.27-505.15s1.31-373.28,7.27-505.15c5.55-121.87,26-188,43-232.13,22.72-58.36,49.78-100,93.5-143.78s85.32-70.88,143.78-93.5c44-17.16,110.26-37.46,232.13-43,131.76-6.06,171.34-7.27,505-7.27S2059.13,666,2191,672c121.87,5.55,188,26,232.13,43,58.36,22.62,100,49.78,143.78,93.5s70.78,85.42,93.5,143.78c17.16,44,37.46,110.26,43,232.13,6.06,131.87,7.27,171.34,7.27,505.15s-1.21,373.28-7.27,505.15c-5.55,121.87-25.95,188.11-43,232.13-22.72,58.36-49.78,100-93.5,143.68s-85.42,70.78-143.78,93.5c-44,17.16-110.26,37.46-232.13,43-131.76,6.06-171.34,7.27-505.15,7.27s-373.28-1.21-505-7.27M1170.5,447.09c-133.07,6.06-224,27.16-303.41,58.06-82.19,31.91-151.86,74.72-221.43,144.18S533.39,788.47,501.48,870.76c-30.9,79.46-52,170.34-58.06,303.41-6.16,133.28-7.57,175.89-7.57,515.35s1.41,382.07,7.57,515.35c6.06,133.08,27.16,223.95,58.06,303.41,31.91,82.19,74.62,152,144.18,221.43s139.14,112.18,221.43,144.18c79.56,30.9,170.34,52,303.41,58.06,133.35,6.06,175.89,7.57,515.35,7.57s382.07-1.41,515.35-7.57c133.08-6.06,223.95-27.16,303.41-58.06,82.19-32,151.86-74.72,221.43-144.18s112.18-139.24,144.18-221.43c30.9-79.46,52.1-170.34,58.06-303.41,6.06-133.38,7.47-175.89,7.47-515.35s-1.41-382.07-7.47-515.35c-6.06-133.08-27.16-224-58.06-303.41-32-82.19-74.72-151.86-144.18-221.43S2586.8,537.06,2504.71,505.15c-79.56-30.9-170.44-52.1-303.41-58.06C2068,441,2025.41,439.52,1686,439.52s-382.1,1.41-515.45,7.57"
                        fill="#ffffff"
                    ></path>
                </g>
            </svg>
        ),
        color: 'bg-pink-600',
    },
}

const posts = [
    {
        id: 1,
        title: 'Lancement de PostFlow.io 🚀',
        variants: '2 Variantes',
        networks: ['Facebook', 'Instagram'],
        time: "Aujourd'hui à 10:00",
        status: 'Publié',
        statusClass: 'bg-emerald-50 text-emerald-600',
        image:
            'https://images.unsplash.com/photo-1611162617474-5b21e879e113?w=100&h=100&fit=crop',
    },
    {
        id: 2,
        title: 'Conseils pour les créateurs',
        variants: '1 Variante',
        networks: ['LinkedIn'],
        time: 'Demain à 14:00',
        status: 'Planifié',
        statusClass: 'bg-orange-50 text-orange-500',
        image:
            'https://images.unsplash.com/photo-1616469829581-73993eb86b02?w=100&h=100&fit=crop',
    },
    {
        id: 3,
        title: 'Notre nouvelle fonctionnalité',
        variants: '3 Variantes',
        networks: ['Facebook', 'Twitter'],
        time: 'Hier à 18:30',
        status: 'Publié',
        statusClass: 'bg-emerald-50 text-emerald-600',
        image:
            'https://images.unsplash.com/photo-1622228514167-938171d182ea?w=100&h=100&fit=crop',
    },
    {
        id: 4,
        title: 'Erreur de connexion API',
        variants: '1 Variante',
        networks: ['Facebook'],
        time: 'Il y a 2 jours',
        status: 'Échec',
        statusClass: 'bg-rose-50 text-rose-500',
        image:
            'https://images.unsplash.com/photo-1594322436404-5a0526db4d13?w=100&h=100&fit=crop',
    },
    {
        id: 5,
        title: 'Mise à jour des CGU',
        variants: '1 Variante',
        networks: ['Twitter'],
        time: 'Il y a 3 jours',
        status: 'Publié',
        statusClass: 'bg-emerald-50 text-emerald-600',
        image:
            'https://images.unsplash.com/photo-1541802052199-651c68e1aade?w=100&h=100&fit=crop',
    },
]

export const RecentPosts = () => {
    return (
        <div className="flex flex-col overflow-hidden rounded-2xl border border-slate-200 bg-white shadow-[0px_4px_16px_rgba(17,17,26,0.02),0px_1px_4px_rgba(17,17,26,0.03)]">
            {/* Header */}
            <div className="flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4 px-7 py-6">
                <h3 className="text-xl font-extrabold text-slate-800 tracking-tight">
                    Activité Récente
                </h3>
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
                            <th className="py-4 px-7 text-[13px] font-bold text-slate-500 w-[40%]">
                                Posts
                            </th>
                            <th className="py-4 px-4 text-[13px] font-bold text-slate-500">
                                Réseaux
                            </th>
                            <th className="py-4 px-4 text-[13px] font-bold text-slate-500">
                                Date prévue
                            </th>
                            <th className="py-4 px-7 text-[13px] font-bold text-slate-500 text-right">
                                Status
                            </th>
                        </tr>
                    </thead>
                    <tbody className="divide-y divide-slate-100">
                        {posts.map(post => (
                            <tr
                                key={post.id}
                                className="transition-colors hover:bg-slate-50/50"
                            >
                                <td className="py-5 px-7">
                                    <div className="flex items-center gap-4">
                                        <div className="h-12 w-16 shrink-0 overflow-hidden rounded-md bg-slate-100 border border-slate-200">
                                            <Image
                                                src={post.image}
                                                alt={post.title}
                                                className="h-full w-full object-cover"
                                            />
                                        </div>
                                        <div className="flex flex-col gap-1 min-w-0">
                                            <p className="truncate font-bold text-slate-800 text-sm">
                                                {post.title}
                                            </p>
                                            <p className="text-xs font-semibold text-slate-500">
                                                {post.variants}
                                            </p>
                                        </div>
                                    </div>
                                </td>
                                <td className="py-5 px-4 text-sm font-semibold text-slate-500">
                                    <div className="flex items-center -space-x-2">
                                        {post.networks.map((network, idx) => {
                                            const platform = platformIcons[network]
                                            if (!platform) return null
                                            return (
                                                <div
                                                    key={network}
                                                    title={network}
                                                    className={`h-7 w-7 rounded-full border-2 border-white flex items-center justify-center text-white ml-[${idx * 8}px]`}
                                                >
                                                    {platform.icon}
                                                </div>
                                            )
                                        })}
                                    </div>
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
    )
}

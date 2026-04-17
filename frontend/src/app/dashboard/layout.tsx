'use client'
import React, { useState } from 'react'
import { DashboardSidebar } from '@/features/dashboard/components/sidebar'
import { TopNavbar } from '@/features/dashboard/components/TopNavbar'

export default function DashboardLayout({
  children,
}: {
  children: React.ReactNode
}) {
  const [isCollapsed, setIsCollapsed] = useState(false)

  return (
    <div className="flex h-screen bg-[#F4F5F7] p-2 sm:p-4 gap-4 sm:gap-6 overflow-hidden text-slate-900 font-inter">
      <div className="h-full hidden md:block transition-all duration-300 ease-in-out">
        <DashboardSidebar isCollapsed={isCollapsed} />
      </div>
      <main className="flex-1 flex flex-col min-w-0 bg-white rounded-2xl sm:rounded-3xl shadow-[0_2px_20px_rgba(0,0,0,0.02)] border border-gray-100 overflow-y-auto w-full relative">
        <TopNavbar onToggleSidebar={() => setIsCollapsed(!isCollapsed)} />
        {children}
      </main>
    </div>
  )
}

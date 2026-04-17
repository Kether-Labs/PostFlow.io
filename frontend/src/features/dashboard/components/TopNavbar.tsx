'use client'

import React from 'react'
import { Search, Bell, Moon, Menu, ChevronDown } from 'lucide-react'
import Image from 'next/image'

interface TopNavbarProps {
  onToggleSidebar?: () => void
}

export const TopNavbar = ({ onToggleSidebar }: TopNavbarProps) => {
  return (
    <header className="sticky top-0 z-50 flex w-full bg-white border-b border-slate-100">
      <div className="flex flex-grow items-center justify-between px-6 py-4 shadow-sm">
        {/* Left Side: Hamburg + Search */}
        <div className="flex items-center gap-4">
          <button
            onClick={onToggleSidebar}
            className="flex cursor-pointer items-center justify-center rounded-md border border-slate-200 bg-white p-2 text-slate-500 hover:bg-slate-50 transition-colors hidden sm:flex active:scale-95"
          >
            <Menu size={20} />
          </button>

          <div className="relative w-full max-w-md hidden sm:block">
            <div className="relative">
              <Search
                className="absolute left-3 top-1/2 -translate-y-1/2 text-slate-400"
                size={18}
              />
              <input
                type="text"
                placeholder="Search or type command..."
                className="w-full rounded-md border border-slate-200 bg-transparent pl-10 pr-12 py-2 text-sm text-slate-800 placeholder-slate-400 focus:outline-none focus:border-[#6366f1] focus:ring-1 focus:ring-[#6366f1] transition-all"
              />
              <span className="absolute right-2 top-1/2 -translate-y-1/2 rounded bg-slate-100 px-1.5 py-0.5 text-[10px] font-bold text-slate-500">
                ⌘K
              </span>
            </div>
          </div>
        </div>

        {/* Right Side: Tools & User */}
        <div className="flex items-center gap-4">
          {/* Dark Mode Toggle */}
          <button className="flex h-10 w-10 items-center justify-center rounded-full bg-slate-50 text-slate-500 hover:text-slate-700 hover:bg-slate-100 transition-colors">
            <Moon size={18} />
          </button>

          {/* Notification Bell */}
          <button className="relative flex h-10 w-10 items-center justify-center rounded-full bg-slate-50 text-slate-500 hover:text-slate-700 hover:bg-slate-100 transition-colors">
            <span className="absolute top-2 right-2.5 h-2 w-2 rounded-full bg-red-500 border-2 border-slate-50"></span>
            <Bell size={18} />
          </button>

          {/* User Profile */}
          <div className="flex items-center gap-3 cursor-pointer pl-2">
            <div className="h-10 w-10 overflow-hidden rounded-full bg-slate-100">
              <Image
                src="https://api.dicebear.com/7.x/avataaars/svg?seed=Musharof"
                alt="User"
                className="h-full w-full object-cover"
              />
            </div>
            <div className="hidden items-center gap-2 sm:flex">
              <span className="text-sm font-semibold text-slate-800">
                Musharof
              </span>
              <ChevronDown size={16} className="text-slate-500" />
            </div>
          </div>
        </div>
      </div>
    </header>
  )
}

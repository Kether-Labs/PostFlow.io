import React from 'react'
import { AuthSidebar } from '@/features/auth/components/AuthSidebar'
import { LoginForm } from '@/features/auth/components/LoginForm/LoginForm'
import { Globe } from 'lucide-react'

export default function LoginPage() {
  return (
    <div className="flex min-h-screen w-full bg-white">
      {/* Sidebar - Hidden on mobile */}
      <AuthSidebar />

      {/* Main Content Area */}
      <main className="relative flex flex-1 flex-col overflow-y-auto">
        {/* Language Switcher - Top Right */}
        <div className="absolute right-8 top-8 z-20 hidden md:block">
          <button className="flex items-center gap-2 text-sm font-medium text-gray-500 hover:text-gray-900 transition-colors">
            <Globe className="h-4 w-4" />
            <span>Français</span>
          </button>
        </div>

        {/* Mobile Header (optional, if you want logo on mobile too) */}
        <div className="flex h-16 items-center px-8 lg:hidden">
          {/* You could add a dark version of the logo here for mobile */}
          <span className="text-xl font-bold bg-[#6366f1] text-white px-2 py-1 rounded">
            PostFlow
          </span>
        </div>

        {/* Login Form Container */}
        <div className="flex flex-1 items-center justify-center">
          <LoginForm />
        </div>
      </main>
    </div>
  )
}

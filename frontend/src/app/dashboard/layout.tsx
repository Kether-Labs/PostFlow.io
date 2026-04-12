import React from "react";
import { DashboardSidebar } from "@/features/dashboard/components/sidebar";

export default function DashboardLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return (
    <div className="flex h-screen bg-[#F4F5F7] p-4 gap-6 overflow-hidden text-slate-900 font-inter">
      <div className="h-full hidden md:block">
        <DashboardSidebar />
      </div>
      <main className="flex-1 flex flex-col min-w-0 bg-white rounded-3xl shadow-[0_2px_20px_rgba(0,0,0,0.02)] border border-gray-100 overflow-y-auto w-full relative">
        {children}
      </main>
    </div>
  );
}

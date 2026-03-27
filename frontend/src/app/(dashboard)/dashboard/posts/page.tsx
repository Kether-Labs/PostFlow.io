"use client"

import Link from 'next/link'
import { useLogout } from '@/features/auth'
import { useCurrentUser } from '@/hooks/useCurrentUser'

export default function DashboardPostsPage() {
  const { user } = useCurrentUser()
  const { mutate: logout, isPending } = useLogout()

  return (
    <div className="min-h-screen p-6 flex flex-col gap-4">
      <h1 className="text-2xl font-semibold">Dashboard / Posts</h1>
      <div className="text-sm text-zinc-600">Bonjour {user?.name || '—'}</div>
      <div className="flex gap-2">
        <Link href="/dashboard" className="h-10 px-3 rounded border grid place-items-center">
          Retour dashboard
        </Link>
        <button
          onClick={() => logout()}
          disabled={isPending}
          className="h-10 px-3 rounded bg-black text-white disabled:opacity-50"
        >
          {isPending ? '...' : 'Logout'}
        </button>
      </div>
    </div>
  )
}

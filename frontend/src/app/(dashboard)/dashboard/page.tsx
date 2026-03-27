"use client"

import Link from 'next/link'
import { useLogout } from '@/features/auth'
import { useCurrentUser } from '@/hooks/useCurrentUser'

export default function DashboardPage() {
  const { user, isAuthenticated } = useCurrentUser()
  const { mutate: logout, isPending } = useLogout()

  return (
    <div className="min-h-screen p-6 flex flex-col gap-4">
      <h1 className="text-2xl font-semibold">Dashboard</h1>
      <div className="rounded border bg-white p-4 text-sm flex flex-col gap-1">
        <div>
          Authentifié: <span className="font-medium">{String(isAuthenticated)}</span>
        </div>
        <div>Nom: {user?.name || '—'}</div>
        <div>Email: {user?.email || '—'}</div>
      </div>
      <div className="flex gap-2">
        <Link
          href="/dashboard/posts"
          className="h-10 px-3 rounded border grid place-items-center"
        >
          Aller à Posts
        </Link>
        <button
          onClick={() => logout()}
          disabled={isPending}
          className="h-10 w-fit px-3 rounded bg-black text-white disabled:opacity-50"
        >
          {isPending ? '...' : 'Logout'}
        </button>
      </div>
    </div>
  )
}

import { NextResponse } from 'next/server'
import type { NextRequest } from 'next/server'

const PROTECTED_ROUTES = ['/dashboard']
const AUTH_ROUTES = ['/login', '/register']

interface PersistedAuthState {
  state: {
    token: string | null
    user: { id: string; name: string; email: string } | null
    isAuthenticated: boolean
  }
  version?: number
}

const parseAuthCookie = (request: NextRequest): string | null => {
  const authCookie = request.cookies.get('postflow-auth')
  if (!authCookie?.value) return null

  try {
    const data = JSON.parse(
      decodeURIComponent(authCookie.value)
    ) as PersistedAuthState

    const token = data?.state?.token
    return typeof token === 'string' && token.length > 0 ? token : null
  } catch {
    return null
  }
}

export function middleware(request: NextRequest) {
  const { pathname } = request.nextUrl
  const token = parseAuthCookie(request)
  const isAuthenticated = token !== null

  const isProtectedRoute = PROTECTED_ROUTES.some(route =>
    pathname.startsWith(route)
  )

  if (isProtectedRoute && !isAuthenticated) {
    const loginUrl = new URL('/login', request.url)
    loginUrl.searchParams.set('redirect', pathname)
    return NextResponse.redirect(loginUrl)
  }

  const isAuthRoute = AUTH_ROUTES.some(route => pathname.startsWith(route))
  if (isAuthRoute && isAuthenticated) {
    return NextResponse.redirect(new URL('/dashboard', request.url))
  }

  return NextResponse.next()
}

export const config = {
  matcher: ['/dashboard/:path*', '/login', '/register'],
}

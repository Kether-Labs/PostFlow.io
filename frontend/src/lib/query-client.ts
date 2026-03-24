import { QueryClient } from '@tanstack/react-query'

export const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      staleTime: 1000 * 60 * 5, // 5 minutes - données "fraîches"
      gcTime: 1000 * 60 * 30, // 30 minutes - temps en cache
      retry: 2, // 2 tentatives si erreur
      refetchOnWindowFocus: false, // Ne pas recharger au focus
    },
  },
})

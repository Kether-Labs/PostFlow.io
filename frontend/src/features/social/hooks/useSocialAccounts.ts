'use client'

import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import env from '@/config/env'
import { socialService } from '@/features/social/services/social.service'
import type {
  ConnectSocialAccountRequest,
  ConnectSocialAccountResponse,
  Platform,
  SocialAccount,
} from '@/features/social/types/social.types'
import { useAuthStore } from '@/store/auth.store'

const socialKeys = {
  accounts: (userId: string) => ['social', 'accounts', userId] as const,
}

export function useSocialAccounts() {
  const userId = useAuthStore(s => s.user?.id) ?? 'anonymous'
  const isAuthenticated = useAuthStore(s => s.isAuthenticated)
  const hasHydrated = useAuthStore(s => s.hasHydrated)

  return useQuery<SocialAccount[], Error>({
    queryKey: socialKeys.accounts(userId),
    queryFn: async () => {
      const res = await socialService.getAccounts()
      return res.accounts
    },
    enabled: env.useMock || (hasHydrated && isAuthenticated),
  })
}

export function useConnectSocialAccount() {
  return useMutation<ConnectSocialAccountResponse, Error, ConnectSocialAccountRequest>(
    {
      mutationFn: input => socialService.getConnectUrl(input),
      onSuccess: ({ redirectUrl }) => {
        window.location.assign(redirectUrl)
      },
    }
  )
}

export function useDisconnectSocialAccount() {
  const qc = useQueryClient()
  const userId = useAuthStore(s => s.user?.id) ?? 'anonymous'

  return useMutation({
    mutationFn: (accountId: string) => socialService.disconnect(accountId),
    onSuccess: async () => {
      await qc.invalidateQueries({ queryKey: socialKeys.accounts(userId) })
    },
  })
}

export function useSocialAccountByPlatform(platform: Platform) {
  const query = useSocialAccounts()

  const account = query.data?.find(a => a.platform === platform) ?? null
  const isConnected = account?.status === 'CONNECTED'

  return { ...query, account, isConnected }
}


import env from "@/config/env"

import {
    mockGetAccountsResponse,
    mockConnectFacebookResponse,
    mockConnectLinkedInResponse,
    mockDisconnectResponse,
} from "@/features/social/mocks/social.mock"

import type {
    GetSocialAccountsResponse,
    ConnectSocialAccountRequest,
    ConnectSocialAccountResponse,
    DisconnectSocialAccountResponse,
    Platform,
} from "@/features/social/types/social.types"

const mockDelay = () =>
    new Promise<void>((resolve) => setTimeout(resolve, 600))

// Récupère le token depuis le store Zustand persisté
const getAuthToken = (): string | null => {
    if (typeof window === "undefined") return null
    try {
        const raw = localStorage.getItem("postflow-auth")
        if (!raw) return null
        const data = JSON.parse(raw) as { state?: { token?: string } }
        return data?.state?.token ?? null
    } catch {
        return null
    }
}

const authHeader = () => {
    const token = getAuthToken()
    return token ? { Authorization: `Bearer ${token}` } : {}
}

const mockConnectResponseByPlatform: Record<
    Platform,
    ConnectSocialAccountResponse
> = {
    FACEBOOK: mockConnectFacebookResponse,
    LINKEDIN: mockConnectLinkedInResponse,
}

export const socialService = {

    getAccounts: async (): Promise<GetSocialAccountsResponse> => {
        if (env.useMock) {
            await mockDelay()
            return mockGetAccountsResponse
        }

        const res = await fetch(`${env.apiUrl}/api/social/accounts`, {
            method: "GET",
            headers: {
                "Content-Type": "application/json",
                ...(authHeader() as Record<string, string>),
            },
        })

        if (!res.ok) {
            const error = await res.json() as { message?: string }
            throw new Error(error.message || "Erreur lors du chargement des comptes")
        }

        return res.json() as Promise<GetSocialAccountsResponse>
    },

    getConnectUrl: async ({
        platform,
        redirectUri,
    }: ConnectSocialAccountRequest): Promise<ConnectSocialAccountResponse> => {
        if (env.useMock) {
            await mockDelay()
            return mockConnectResponseByPlatform[platform]
        }

        const res = await fetch(`${env.apiUrl}/api/social/connect`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                ...(authHeader() as Record<string, string>),
            },
            body: JSON.stringify({ platform, redirectUri }),
        })

        if (!res.ok) {
            const error = await res.json() as { message?: string }
            throw new Error(error.message || "Erreur lors de la connexion")
        }

        return res.json() as Promise<ConnectSocialAccountResponse>
    },

    disconnect: async (
        accountId: string
    ): Promise<DisconnectSocialAccountResponse> => {
        if (env.useMock) {
            await mockDelay()
            return mockDisconnectResponse
        }

        const res = await fetch(
            `${env.apiUrl}/api/social/accounts/${accountId}`,
            {
                method: "DELETE",
                headers: {
                    "Content-Type": "application/json",
                    ...(authHeader() as Record<string, string>),
                },
            }
        )

        if (!res.ok) {
            const error = await res.json() as { message?: string }
            throw new Error(error.message || "Erreur lors de la déconnexion")
        }

        return res.json() as Promise<DisconnectSocialAccountResponse>
    },
}
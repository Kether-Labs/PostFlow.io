import { mockUser } from "@/features/auth/mocks/auth.mock";
import type { ConnectSocialAccountResponse, DisconnectSocialAccountResponse, GetSocialAccountsResponse, SocialAccount } from "../types/social.types";


export const mockFacebookAccount: SocialAccount = {
    id: "acc_1234567890",
    platform: "FACEBOOK",
    platformUserId: "12345678909094667475737646",
    name: "PostFlow",
    avatar: "https://ui-avatars.com/api/?name=PostFlow&background=1877F2&color=fff",
    status: "CONNECTED",
    connectedAt: new Date(Date.now() - 7 * 24 * 60 * 60 * 1000).toISOString(),
    expiresAt: new Date(Date.now() + 53 * 24 * 60 * 60 * 1000).toISOString(),
}

export const mockLinkedInAccount: SocialAccount = {
    id: "acc_1234567891",
    platform: "LINKEDIN",
    platformUserId: "1234567891",
    name: mockUser.name,
    avatar: "https://ui-avatars.com/api/?name=PostFlow&background=0A66C2&color=fff",
    status: "EXPIRED",
    connectedAt: new Date(Date.now() - 7 * 24 * 60 * 60 * 1000).toISOString(),
    expiresAt: new Date(Date.now() + 53 * 24 * 60 * 60 * 1000).toISOString(),
}

export const mockGetAccountsResponse: GetSocialAccountsResponse = {
    success: true,
    accounts: [mockFacebookAccount, mockLinkedInAccount],
}

// Liste vide — pour tester le onboarding (aucun compte connecté)
export const mockGetAccountsEmptyResponse: GetSocialAccountsResponse = {
    success: true,
    accounts: [],
}

// Connexion Facebook — retourne une URL OAuth fictive
export const mockConnectFacebookResponse: ConnectSocialAccountResponse = {
    success: true,
    redirectUrl: "https://www.facebook.com/v18.0/dialog/oauth?mock=true&platform=facebook",
}

// Connexion LinkedIn
export const mockConnectLinkedInResponse: ConnectSocialAccountResponse = {
    success: true,
    redirectUrl: "https://www.linkedin.com/oauth/v2/authorization?mock=true&platform=linkedin",
}

// Déconnexion réussie
export const mockDisconnectResponse: DisconnectSocialAccountResponse = {
    success: true,
    message: "Compte déconnecté avec succès",
}
export const mockSocialAccounts: SocialAccount[] = [mockFacebookAccount, mockLinkedInAccount]

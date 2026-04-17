
export type Platform = 'FACEBOOK' | 'LINKEDIN'


export type SocialAccountStatus =
  | 'CONNECTED' // compte actif, token valide
  | 'EXPIRED' // token expiré — reconnexion requise
  | 'ERROR' // erreur lors de la publication
  | 'DISCONNECTED' // déconnecté manuellement


export interface SocialAccount {
  id: string
  platform: Platform
  platformUserId: string // ID de l'utilisateur sur la plateforme
  name: string // Nom de la page ou du profil
  avatar: string | null // URL de la photo de profil
  status: SocialAccountStatus
  connectedAt: string // ISO 8601
  expiresAt: string | null // null si pas d'expiration connue
}

// ── Réponses API ─────────────────────────────────────────
export interface GetSocialAccountsResponse {
  success: boolean
  accounts: SocialAccount[]
}

export interface ConnectSocialAccountResponse {
  success: boolean
  redirectUrl: string // URL OAuth vers laquelle rediriger l'utilisateur
}

export interface DisconnectSocialAccountResponse {
  success: boolean
  message: string
}


export interface ConnectSocialAccountRequest {
  platform: Platform
  redirectUri: string // URL de callback après OAuth
}

export interface DisconnectSocialAccountRequest {
  accountId: string
}


export interface PlatformConfig {
  platform: Platform
  label: string
  color: string // couleur brand
  icon: string // nom de l'icône lucide-react
}

export const PLATFORM_CONFIG: Record<Platform, PlatformConfig> = {
  FACEBOOK: {
    platform: 'FACEBOOK',
    label: 'Facebook',
    color: '#1877F2',
    icon: 'Facebook',
  },
  LINKEDIN: {
    platform: 'LINKEDIN',
    label: 'LinkedIn',
    color: '#0A66C2',
    icon: 'Linkedin',
  },
}

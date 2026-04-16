export type {
  Platform,
  SocialAccountStatus,
  SocialAccount,
  GetSocialAccountsResponse,
  ConnectSocialAccountResponse,
  DisconnectSocialAccountResponse,
  ConnectSocialAccountRequest,
  DisconnectSocialAccountRequest,
  PlatformConfig,
} from './types/social.types'

export { PLATFORM_CONFIG } from './types/social.types'

export {
  useSocialAccounts,
  useConnectSocialAccount,
  useDisconnectSocialAccount,
  useSocialAccountByPlatform,
} from './hooks/useSocialAccounts'

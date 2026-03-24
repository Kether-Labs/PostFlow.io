const env = {
  apiUrl: process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080',
  useMock: process.env.NEXT_PUBLIC_USE_MOCK === 'true',
  appName: process.env.NEXT_PUBLIC_APP_NAME || 'PostFlow.io',
  metaAppId: process.env.NEXT_PUBLIC_META_APP_ID || '',
}

export default env

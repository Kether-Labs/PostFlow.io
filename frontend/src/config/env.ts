const getEnvVar = (key: string, required = true): string => {
  const value = process.env[key]

  if (!value && required) {
    throw new Error(
      `Variable d'environnement manquante : ${key}
            Vérifie ton fichier .env.local`
    )
  }

  return value || ''
}

const env = {
  apiUrl: getEnvVar('NEXT_PUBLIC_API_URL'),
  metaAppId: getEnvVar('NEXT_PUBLIC_META_APP_ID'),
  appName: getEnvVar('NEXT_PUBLIC_APP_NAME', false), // false = optionnel
}

export default env

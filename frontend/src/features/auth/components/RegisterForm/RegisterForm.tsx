'use client'

import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { Eye, EyeOff, Loader2, Check, X } from 'lucide-react'
import { useState } from 'react'
import Link from 'next/link'

import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/shad_ui/input'
import { Label } from '@/components/ui/shad_ui/label'
import { Alert, AlertDescription } from '@/components/ui/shad_ui/alert'
import { useRegister } from '@/features/auth'
import { registerSchema, type RegisterFormData } from './RegisterForm.types'

// Indicateur de force du mot de passe
const PasswordRule = ({ valid, label }: { valid: boolean; label: string }) => (
  <div className="flex items-center gap-2 text-xs">
    {valid ? (
      <Check className="h-3 w-3 text-green-500" />
    ) : (
      <X className="h-3 w-3 text-muted-foreground" />
    )}
    <span className={valid ? 'text-green-600' : 'text-muted-foreground'}>
      {label}
    </span>
  </div>
)

export const RegisterForm = () => {
  const [showPassword, setShowPassword] = useState(false)
  const [showConfirm, setShowConfirm] = useState(false)

  // TanStack Query mutation
  const { mutate: register, isPending, isError, error } = useRegister()

  const {
    register: registerField,
    handleSubmit,
    watch,
    formState: { errors },
  } = useForm<RegisterFormData>({
    resolver: zodResolver(registerSchema),
  })

  // Surveiller le mot de passe pour l'indicateur de force
  const password = watch('password', '')

  const passwordRules = [
    { valid: password.length >= 8, label: '8 caractères minimum' },
    { valid: /[A-Z]/.test(password), label: 'Une majuscule' },
    { valid: /[0-9]/.test(password), label: 'Un chiffre' },
  ]

  const onSubmit = (data: RegisterFormData) => {
    register({
      name: data.name,
      email: data.email,
      password: data.password,
    })
  }

  return (
    <div className="w-full max-w-md space-y-6">
      {/* Titre */}
      <div className="space-y-2 text-center">
        <h1 className="font-display text-2xl font-bold text-primary">
          Créer un compte
        </h1>
        <p className="text-sm text-muted-foreground">
          Commencez à planifier vos publications gratuitement
        </p>
      </div>

      {/* Erreur API */}
      {isError && (
        <Alert variant="destructive">
          <AlertDescription>
            {error?.message || "Une erreur est survenue lors de l'inscription"}
          </AlertDescription>
        </Alert>
      )}

      {/* Formulaire */}
      <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
        {/* Nom */}
        <div className="space-y-2">
          <Label htmlFor="name">Nom complet</Label>
          <Input
            id="name"
            type="text"
            placeholder="Jean Dupont"
            disabled={isPending}
            {...registerField('name')}
          />
          {errors.name && (
            <p className="text-xs text-destructive">{errors.name.message}</p>
          )}
        </div>

        {/* Email */}
        <div className="space-y-2">
          <Label htmlFor="email">Email</Label>
          <Input
            id="email"
            type="email"
            placeholder="jean@postflow.io"
            disabled={isPending}
            {...registerField('email')}
          />
          {errors.email && (
            <p className="text-xs text-destructive">{errors.email.message}</p>
          )}
        </div>

        {/* Mot de passe */}
        <div className="space-y-2">
          <Label htmlFor="password">Mot de passe</Label>
          <div className="relative">
            <Input
              id="password"
              type={showPassword ? 'text' : 'password'}
              placeholder="••••••••"
              disabled={isPending}
              className="pr-10"
              {...registerField('password')}
            />
            <button
              type="button"
              onClick={() => setShowPassword(!showPassword)}
              className="absolute right-3 top-1/2 -translate-y-1/2
                         text-muted-foreground hover:text-foreground"
            >
              {showPassword ? (
                <EyeOff className="h-4 w-4" />
              ) : (
                <Eye className="h-4 w-4" />
              )}
            </button>
          </div>

          {/* Indicateur de force */}
          {password.length > 0 && (
            <div className="space-y-1 pt-1">
              {passwordRules.map(rule => (
                <PasswordRule
                  key={rule.label}
                  valid={rule.valid}
                  label={rule.label}
                />
              ))}
            </div>
          )}

          {errors.password && (
            <p className="text-xs text-destructive">
              {errors.password.message}
            </p>
          )}
        </div>

        {/* Confirmation mot de passe */}
        <div className="space-y-2">
          <Label htmlFor="confirmPassword">Confirmer le mot de passe</Label>
          <div className="relative">
            <Input
              id="confirmPassword"
              type={showConfirm ? 'text' : 'password'}
              placeholder="••••••••"
              disabled={isPending}
              className="pr-10"
              {...registerField('confirmPassword')}
            />
            <button
              type="button"
              onClick={() => setShowConfirm(!showConfirm)}
              className="absolute right-3 top-1/2 -translate-y-1/2
                         text-muted-foreground hover:text-foreground"
            >
              {showConfirm ? (
                <EyeOff className="h-4 w-4" />
              ) : (
                <Eye className="h-4 w-4" />
              )}
            </button>
          </div>
          {errors.confirmPassword && (
            <p className="text-xs text-destructive">
              {errors.confirmPassword.message}
            </p>
          )}
        </div>

        {/* Bouton submit */}
        <Button type="submit" className="w-full" disabled={isPending}>
          {isPending ? (
            <>
              <Loader2 className="mr-2 h-4 w-4 animate-spin" />
              Création du compte...
            </>
          ) : (
            'Créer mon compte'
          )}
        </Button>
      </form>

      {/* Lien connexion */}
      <p className="text-center text-sm text-muted-foreground">
        Déjà un compte ?{' '}
        <Link href="/login" className="text-accent font-medium hover:underline">
          Se connecter
        </Link>
      </p>
    </div>
  )
}

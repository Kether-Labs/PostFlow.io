'use client'

import React, { useState } from 'react'
import Link from 'next/link'
import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { Eye, EyeOff, Mail, Lock } from 'lucide-react'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/shad_ui/input'
import { loginSchema, type LoginFormData } from '../../types/auth.schemas'

export const LoginForm = () => {
  const [showPassword, setShowPassword] = useState(false)
  const [isLoading, setIsLoading] = useState(false)

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<LoginFormData>({
    resolver: zodResolver(loginSchema),
  })

  const onSubmit = async (data: LoginFormData) => {
    setIsLoading(true)
    // Simulation d'appel API
    console.log('Données de connexion:', data)
    setTimeout(() => setIsLoading(false), 1500)
  }

  return (
    <div className="flex w-full flex-col justify-center px-4 py-12 sm:px-12">
      <div className="mx-auto w-full max-w-md space-y-8">
        {/* Header */}
        <div className="text-center">
          <h2 className="text-3xl font-bold tracking-tight text-gray-900">
            Se connecter à votre compte
          </h2>
          <p className="mt-2  text-sm text-gray-500">
            Votre outil IA pour engager, vendre et accélérer votre croissance.
          </p>
        </div>

        {/* Social Login */}
        <div className="mt-8">
          <Button
            variant="outline"
            className="w-full cursor-pointer flex items-center justify-center gap-3 py-6 text-base font-medium transition-all hover:bg-gray-50 rounded-xl"
          >
            <svg className="h-5 w-5" viewBox="0 0 24 24">
              <path
                fill="#4285F4"
                d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92c-.26 1.37-1.04 2.53-2.21 3.31v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.09z"
              />
              <path
                fill="#34A853"
                d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z"
              />
              <path
                fill="#FBBC05"
                d="M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.07H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.93l3.66-2.84z"
              />
              <path
                fill="#EA4335"
                d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.47 2.18 7.07l3.66 2.84c.87-2.6 3.3-4.53 1.16-4.53z"
              />
            </svg>
            Continuer avec Google
          </Button>
        </div>

        {/* Separator */}
        <div className="relative">
          <div className="absolute inset-0 flex items-center">
            <div className="w-full border-t border-gray-100"></div>
          </div>
          <div className="relative flex justify-center text-sm font-medium">
            <span className="bg-white px-4 text-gray-400">ou</span>
          </div>
        </div>

        {/* Form */}
        <form onSubmit={handleSubmit(onSubmit)} className="space-y-5">
          <div className="space-y-1">
            <div className="relative">
              <Mail className="absolute left-4 top-1/2 h-5 w-5 -translate-y-1/2 text-gray-400" />
              <Input
                {...register('email')}
                type="email"
                placeholder="Entrez votre email"
                className={`pl-12 pr-12 py-6 text-base rounded-xl border-gray-200 focus:border-[#6366f1] focus:ring-[#6366f1] transition-all ${errors.email ? 'border-red-500' : ''}`}
              />
            </div>
            {errors.email && (
              <p className="text-xs text-red-500 font-medium ml-1">
                {errors.email.message}
              </p>
            )}
          </div>

          <div className="space-y-1">
            <div className="relative">
              <Lock className="absolute left-4 top-1/2 h-5 w-5 -translate-y-1/2 text-gray-400" />
              <Input
                {...register('password')}
                type={showPassword ? 'text' : 'password'}
                placeholder="Entrez votre mot de passe"
                className={`pl-12 pr-12 py-6 text-base rounded-xl border-gray-200 focus:border-[#6366f1] focus:ring-[#6366f1] transition-all ${errors.password ? 'border-red-500' : ''}`}
              />
              <button
                type="button"
                onClick={() => setShowPassword(!showPassword)}
                className="absolute right-4 top-1/2 -translate-y-1/2 text-gray-400 hover:text-gray-600 transition-colors"
              >
                {showPassword ? (
                  <EyeOff className="h-5 w-5" />
                ) : (
                  <Eye className="h-5 w-5" />
                )}
              </button>
            </div>
            {errors.password && (
              <p className="text-xs text-red-500 font-medium ml-1">
                {errors.password.message}
              </p>
            )}
            <div className="flex justify-end mt-1">
              <Link
                href="/forgot-password"
                title="Mot de passe oublié ?"
                className="text-sm font-semibold text-[#6366f1] hover:text-[#4f46e5]"
              >
                Mot de passe oublié ?
              </Link>
            </div>
          </div>

          <Button
            type="submit"
            disabled={isLoading}
            className="w-full cursor-pointer py-6 text-base font-bold bg-[#6366f1] hover:bg-[#4f46e5] text-white rounded-xl shadow-lg shadow-indigo-200 transition-all hover:scale-[1.01] active:scale-[0.99]"
          >
            {isLoading ? 'Connexion en cours...' : 'Se connecter'}
          </Button>
        </form>

        {/* Footer Link */}
        <p className="text-center text-sm text-gray-600">
          Pas encore de compte ?{' '}
          <Link
            href="/register"
            className="font-bold text-[#6366f1] hover:underline"
          >
            S'inscrire gratuitement
          </Link>
        </p>

        {/* Legal Links */}
        <div className="pt-8 flex justify-center gap-6 text-xs text-gray-400 font-medium">
          <Link
            href="/privacy"
            className="hover:text-gray-600 transition-colors"
          >
            Politique de Confidentialité
          </Link>
          <Link href="/terms" className="hover:text-gray-600 transition-colors">
            Conditions d'Utilisation
          </Link>
        </div>
      </div>
    </div>
  )
}

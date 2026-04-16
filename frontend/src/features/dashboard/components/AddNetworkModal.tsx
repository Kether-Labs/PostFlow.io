'use client'

import React, { useEffect } from 'react'
import { motion, AnimatePresence } from 'framer-motion'
import { X, Check } from 'lucide-react'

interface AddNetworkModalProps {
  isOpen: boolean
  onClose: () => void
}

const networks = [
  {
    id: 'facebook',
    name: 'Facebook',
    subtitle: '(Meta Business Suite)',
    description: 'Pages Facebook',
    logoColor: 'bg-blue-600',
    letter: 'f',
    connected: true,
  },
  {
    id: 'instagram',
    name: 'Instagram',
    subtitle: '(Meta Business Suite)',
    description: 'Comptes Professionnels ou Créateurs',
    logoColor: 'bg-gradient-to-tr from-yellow-400 via-pink-500 to-purple-500',
    letter: 'IG',
    connected: false,
  },
  {
    id: 'tiktok',
    name: 'TikTok',
    subtitle: '',
    description: 'Comptes Professionnels',
    logoColor: 'bg-black',
    letter: 't',
    connected: false,
  },
  {
    id: 'youtube',
    name: 'YouTube',
    subtitle: '',
    description: '',
    logoColor: 'bg-red-600',
    letter: 'Y',
    connected: false,
  },
]

export const AddNetworkModal = ({ isOpen, onClose }: AddNetworkModalProps) => {
  // Prevent scrolling on mount when open
  useEffect(() => {
    if (isOpen) {
      document.body.style.overflow = 'hidden'
    } else {
      document.body.style.overflow = 'unset'
    }
    return () => {
      document.body.style.overflow = 'unset'
    }
  }, [isOpen])

  return (
    <AnimatePresence>
      {isOpen && (
        <>
          <motion.div
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            exit={{ opacity: 0 }}
            onClick={onClose}
            className="fixed inset-0 z-[100] bg-slate-900/40 backdrop-blur-sm"
          />
          <motion.div
            initial={{ opacity: 0, scale: 0.95, y: 20 }}
            animate={{ opacity: 1, scale: 1, y: 0 }}
            exit={{ opacity: 0, scale: 0.95, y: 20 }}
            className="fixed left-1/2 top-1/2 z-[101] w-full max-w-[480px] -translate-x-1/2 -translate-y-1/2 rounded-2xl bg-white p-6 shadow-2xl"
          >
            {/* Header */}
            <div className="flex items-start justify-between mb-2">
              <h2 className="text-xl font-bold text-slate-800 font-outfit">
                Connecter un nouveau réseau
              </h2>
              <button
                onClick={onClose}
                className="rounded-full p-2 text-gray-400 hover:bg-gray-100 hover:text-slate-600 transition-colors"
                aria-label="Close"
              >
                <X className="h-4 w-4" />
              </button>
            </div>

            <p className="text-sm text-slate-600 mb-6">
              Si vous avez besoin d'aide, vous pouvez{' '}
              <a
                href="#"
                className="font-semibold text-[#7B51FA] hover:underline"
              >
                Suivre notre guide
              </a>{' '}
              ou{' '}
              <a
                href="#"
                className="font-semibold text-[#7B51FA] hover:underline"
              >
                Contacter le support
              </a>
              .
            </p>

            {/* List */}
            <div className="space-y-3">
              {networks.map(network => (
                <div
                  key={network.id}
                  className="flex items-center justify-between rounded-xl border border-gray-100 p-4 transition-all hover:border-gray-200 hover:shadow-sm cursor-pointer group"
                >
                  <div className="flex items-center gap-4">
                    <div
                      className={`flex h-10 w-10 items-center justify-center rounded-full ${network.logoColor} text-white font-bold text-lg shadow-sm group-hover:scale-105 transition-transform`}
                    >
                      {network.letter}
                    </div>
                    <div className="flex flex-col">
                      <div className="flex items-center gap-1.5">
                        <span className="font-bold text-slate-800">
                          {network.name}
                        </span>
                        {network.subtitle && (
                          <span className="text-xs text-gray-400 font-medium">
                            {network.subtitle}
                          </span>
                        )}
                        {network.connected && (
                          <div className="flex h-[14px] w-[14px] items-center justify-center rounded-full bg-slate-800 ml-1">
                            <Check className="h-2 w-2 text-white stroke-[3px]" />
                          </div>
                        )}
                      </div>
                      {network.description && (
                        <span className="text-xs text-slate-500 mt-0.5">
                          {network.description}
                        </span>
                      )}
                    </div>
                  </div>
                  {/* Additional Actions placeholder, optional three dots from mockup */}
                  {network.connected && (
                    <button className="text-gray-300 hover:text-slate-600 font-bold tracking-widest px-2 pb-1">
                      ...
                    </button>
                  )}
                </div>
              ))}
            </div>
          </motion.div>
        </>
      )}
    </AnimatePresence>
  )
}

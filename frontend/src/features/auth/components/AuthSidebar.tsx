'use client'

import React, { useEffect, useState } from 'react'
import Image from 'next/image'
import { motion, AnimatePresence } from 'framer-motion'
import logoWhite from '@/assets/images/logo-white.png'

const testimonials = [
  {
    id: 1,
    content:
      'On répond instantanément maintenant. Notre engagement et notre reach ont explosé.',
    author: 'Alex Rafaitin',
    role: 'Fondateur @Père&Fish',
    avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Alex',
  },
  {
    id: 2,
    content:
      'PostFlow a transformé notre gestion des réseaux sociaux. Un gain de temps incroyable.',
    author: 'Marie Dupont',
    role: 'Marketing Manager',
    avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Marie',
  },
  {
    id: 3,
    content:
      "L'IA de PostFlow est bluffante. Les contenus générés sont d'une qualité exceptionnelle.",
    author: 'Thomas Legrand',
    role: 'Créateur de contenu',
    avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Thomas',
  },
]

const partners = [
  { name: 'Bexley', logo: 'Bexley' },
  { name: 'G20', logo: 'G20' },
  { name: 'SPARKLE', logo: 'SPARKLE' },
  { name: 'JACOB', logo: 'JACOB' },
]

export const AuthSidebar = () => {
  const [currentIndex, setCurrentIndex] = useState(0)

  useEffect(() => {
    const timer = setInterval(() => {
      setCurrentIndex(prev => (prev + 1) % testimonials.length)
    }, 5000)
    return () => clearInterval(timer)
  }, [])

  return (
    <div className="relative hidden w-full flex-col justify-between bg-[#6366f1] p-12 text-white lg:flex lg:w-1/3 min-h-screen overflow-hidden">
      {/* Background patterns/effects */}
      <div className="absolute inset-0 opacity-10">
        <div className="absolute -left-20 -top-20 h-64 w-64 rounded-full bg-white blur-3xl" />
        <div className="absolute -right-20 -bottom-20 h-96 w-96 rounded-full bg-white blur-3xl" />
      </div>

      {/* Logo */}
      <div className="relative z-10">
        <Image
          src={logoWhite}
          alt="PostFlow.io Logo"
          width={160}
          height={40}
          className="h-auto w-auto"
        />
      </div>

      {/* Testimonials Slider */}
      <div className="relative z-10 flex flex-1 flex-col justify-center py-20">
        <div className="relative h-[280px] w-full max-w-md mx-auto">
          <AnimatePresence mode="wait">
            <motion.div
              key={currentIndex}
              initial={{ opacity: 0, scale: 0.9, y: 20 }}
              animate={{ opacity: 1, scale: 1, y: 0 }}
              exit={{ opacity: 0, scale: 0.9, y: -20 }}
              transition={{ duration: 0.5, ease: 'easeOut' }}
              className="absolute inset-0 flex flex-col justify-between rounded-3xl bg-white p-8 text-[#1f2937] shadow-2xl"
            >
              <div className="flex flex-col gap-4">
                <div className="flex gap-1">
                  {[1, 2, 3, 4, 5].map(s => (
                    <svg
                      key={s}
                      className="h-5 w-5 fill-yellow-400"
                      viewBox="0 0 20 20"
                    >
                      <path d="M9.049 2.927c.3-.921 1.603-.921 1.902 0l1.07 3.292a1 1 0 00.95.69h3.462c.969 0 1.371 1.24.588 1.81l-2.8 2.034a1 1 0 00-.364 1.118l1.07 3.292c.3.921-.755 1.688-1.54 1.118l-2.8-2.034a1 1 0 00-1.175 0l-2.8 2.034c-.784.57-1.838-.197-1.539-1.118l1.07-3.292a1 1 0 00-.364-1.118L2.98 8.72c-.783-.57-.38-1.81.588-1.81h3.461a1 1 0 00.951-.69l1.07-3.292z" />
                    </svg>
                  ))}
                </div>
                <p className="text-xl font-medium leading-relaxed">
                  "{testimonials[currentIndex].content}"
                </p>
              </div>

              <div className="mt-8 flex items-center gap-4">
                <div className="relative h-12 w-12 overflow-hidden rounded-full border-2 border-gray-100">
                  <Image
                    width={48}
                    height={48}
                    src={testimonials[currentIndex].avatar}
                    alt={testimonials[currentIndex].author}
                    className="h-full w-full object-cover"
                  />
                </div>
                <div>
                  <h4 className="font-bold text-gray-900">
                    {testimonials[currentIndex].author}
                  </h4>
                  <p className="text-sm text-gray-500">
                    {testimonials[currentIndex].role}
                  </p>
                </div>
              </div>
            </motion.div>
          </AnimatePresence>
        </div>

        {/* Pagination Dots */}
        <div className="mt-12 flex justify-center gap-2">
          {testimonials.map((_, index) => (
            <button
              key={index}
              onClick={() => setCurrentIndex(index)}
              className={`h-2 rounded-full transition-all duration-300 ${
                currentIndex === index ? 'w-8 bg-white' : 'w-2 bg-white/40'
              }`}
            />
          ))}
        </div>
      </div>

      {/* Social Proof */}
      <div className="relative z-10">
        <p className="mb-6 text-center text-sm font-medium text-white/70">
          Utilisé par les meilleurs{' '}
          <span className="rounded bg-white/20 px-1 text-white">créateurs</span>{' '}
          quotidiennement
        </p>
        <div className="flex flex-wrap justify-center gap-x-12 gap-y-6 opacity-60 grayscale invert brightness-0">
          {partners.map(partner => (
            <div
              key={partner.name}
              className="text-lg font-black tracking-widest"
            >
              {partner.name}
            </div>
          ))}
        </div>
      </div>
    </div>
  )
}

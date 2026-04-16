'use client'

import React from 'react'
import Link from 'next/link'
import { CircleAlert } from 'lucide-react'
import { motion } from 'framer-motion'

export const AlertBanner = () => {
  return (
    <motion.div
      initial={{ opacity: 0, scale: 0.98 }}
      animate={{ opacity: 1, scale: 1 }}
      transition={{ duration: 0.3 }}
      className="flex flex-col sm:flex-row items-center justify-between gap-4 rounded-xl border border-rose-500/20 bg-rose-500/10 p-4 shadow-sm"
    >
      <div className="flex items-center gap-3">
        <div className="flex h-6 w-6 items-center justify-center rounded-full bg-rose-500">
          <CircleAlert className="h-4 w-4 text-white" />
        </div>
        <p className="text-sm font-medium text-rose-400">
          <strong className="font-semibold text-rose-300">
            2 posts ont échoué
          </strong>{' '}
          — Token Facebook expiré, reconnexion requise.
        </p>
      </div>
      <Link
        href="/dashboard/posts?status=failed"
        className="text-sm font-semibold text-rose-300 hover:text-rose-200 underline underline-offset-4 decoration-rose-500/50 hover:decoration-rose-300"
      >
        Voir les posts
      </Link>
    </motion.div>
  )
}

import { Avatar as ShadcnAvatar,AvatarImage, AvatarFallback,AvatarBadge } from "@/components/ui/avatar";
import { cn } from "@/lib/utils";

interface AvatarProps {
  name: string           // Nom complet pour extraire les initiales
  src?: string          // URL de l'image (optionnel)
  size?: 'sm' | 'md' | 'lg'  // défaut: 'md'
  className?: string
}

const sizeClasses = {
  sm: "h-8 w-8 text-xs",
  md: "h-10 w-10 text-sm",
  lg: "h-12 w-12 text-base",
}

// Fonction pour extraire les initiales
const getInitials = (name: string): string => {
  return name
    .split(" ")
    .map((n) => n[0])
    .join("")
    .toUpperCase()
    .slice(0, 2)
}

// Fonction pour générer une couleur basée sur le nom
const getColorFromName = (name: string): string => {
  const colors = [
    "bg-red-500",
    "bg-blue-500",
    "bg-green-500",
    "bg-yellow-500",
    "bg-purple-500",
    "bg-pink-500",
    "bg-indigo-500",
    "bg-teal-500",
  ]
  const hash = name.split("").reduce((acc, char) => acc + char.charCodeAt(0), 0)
  return colors[hash % colors.length]
}
const Avater = ({name,size="md",src="",className=""}:AvatarProps) => {
  return (
    <div>
      <ShadcnAvatar className={cn(sizeClasses[size],className)}>
        <AvatarImage src={src} alt={name} />
        <AvatarFallback>{getInitials(name)}</AvatarFallback>
        <AvatarBadge className=""/>
    </ShadcnAvatar>
    </div>
  );
}

// export default avatar;
export default Avater;
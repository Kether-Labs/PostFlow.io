"use client"
import Badge from "./ui/Badge";
import Spinner from "./ui/Spinner";

import Avatar from "./ui/Avater"
// import { Alert } from "./ui/Alert";

const ComponentsUsage = () => {
    return (
        <div className="w-full flex flex-wrap gap-4 px-4">
            <Spinner size="lg" />
            <Spinner className="text-blue-500" />

            <Avatar name="mzirkof Alonzo" />

            <Badge variant="draft">Brouillon</Badge>
            <Badge variant="scheduled">Planifié</Badge>
            <Badge variant="published">Publié</Badge>
            <Badge variant="failed">Échec</Badge>

            {/* <Alert variant="error" title="Erreur">
                Impossible de charger les données
            </Alert> */}

            {/* <Alert variant="success" dismissible onDismiss={() => console.log('fermé')}>
                Compte créé avec succès !
            </Alert> */}

            {/* <Alert variant="info" title="Information">
                Votre abonnement expire dans 3 jours.
            </Alert> */}
        </div>
    );
}

export default ComponentsUsage;
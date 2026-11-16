import fs from 'fs';
import path from 'path';
import { fileURLToPath } from 'url';

// Para obter __dirname em ES Modules
const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);

// Lê o db.json original
const rawData = fs.readFileSync('db.json', 'utf8');
const data = JSON.parse(rawData);

// Cria a pasta downloads se não existir
const downloadsDir = path.join(__dirname, '../public/downloads');
if (!fs.existsSync(downloadsDir)) {
    fs.mkdirSync(downloadsDir, { recursive: true });
}

// Processa cada continente e cada cidade
data.continents.forEach(continent => {
    continent.cities.forEach(city => {
        const citySlug = city.name.toLowerCase()
            .normalize("NFD").replace(/[\u0300-\u036f]/g, "")
            .replace(/\s+/g, '_')
            .replace(/[^a-z0-9_]/g, '');

        const countrySlug = (city.country || 'Portugal').toLowerCase()
            .normalize("NFD").replace(/[\u0300-\u036f]/g, "")
            .replace(/\s+/g, '_')
            .replace(/[^a-z0-9_]/g, '');

        // 1. Ficheiro de informações gerais da cidade
        const generalInfo = {
            id: city.id,
            name: city.name,
            slug: city.slug,
            country: city.country || 'Portugal',
            image: city.image || '',
            coords: city.coords || { lat: 0, lng: 0, zoom: 10 },
            continent_id: continent.id,
            continent_name: continent.name
        };

        // JSON
        fs.writeFileSync(
            path.join(downloadsDir, `${citySlug}_${countrySlug}_general.json`),
            JSON.stringify(generalInfo, null, 2),
            'utf8'
        );

        // CSV
        const coords = city.coords || { lat: 0, lng: 0, zoom: 10 };
        const generalCSV = `id,name,slug,country,image,lat,lng,zoom,continent_id,continent_name\n${city.id},"${city.name}","${city.slug}","${city.country || 'Portugal'}","${city.image || ''}",${coords.lat},${coords.lng},${coords.zoom},${continent.id},"${continent.name}"`;
        fs.writeFileSync(
            path.join(downloadsDir, `${citySlug}_${countrySlug}_general.csv`),
            generalCSV,
            'utf8'
        );

        // 2. Ficheiro de listings da cidade
        // Busca listings pelo slug da cidade
        const cityListings = data.listings[city.slug] || [];

        // Adiciona city_id a cada listing para referência futura
        const listingsWithCityId = cityListings.map(listing => ({
            ...listing,
            city_id: city.id,
            city_name: city.name,
            city_slug: city.slug
        }));

        // JSON
        fs.writeFileSync(
            path.join(downloadsDir, `${citySlug}_${countrySlug}_listings.json`),
            JSON.stringify(listingsWithCityId, null, 2),
            'utf8'
        );

        // CSV
        if (listingsWithCityId.length > 0) {
            // Encontra todos os campos únicos
            const allKeys = new Set();
            listingsWithCityId.forEach(listing => {
                if (listing && typeof listing === 'object') {
                    Object.keys(listing).forEach(key => allKeys.add(key));
                }
            });

            const headers = Array.from(allKeys).join(',');
            const rows = listingsWithCityId.map(listing => {
                if (!listing) return '';
                return Array.from(allKeys).map(key => {
                    const value = listing[key];
                    if (value === undefined || value === null) return '';
                    if (typeof value === 'string') {
                        return `"${value.replace(/"/g, '""')}"`;
                    }
                    if (typeof value === 'object') {
                        return `"${JSON.stringify(value).replace(/"/g, '""')}"`;
                    }
                    return value;
                }).join(',');
            }).filter(row => row !== '');

            const listingsCSV = [headers, ...rows].join('\n');
            fs.writeFileSync(
                path.join(downloadsDir, `${citySlug}_${countrySlug}_listings.csv`),
                listingsCSV,
                'utf8'
            );
        } else {
            // Cria ficheiro vazio com cabeçalhos básicos
            fs.writeFileSync(
                path.join(downloadsDir, `${citySlug}_${countrySlug}_listings.csv`),
                'id,city_id,city_name,city_slug,name,latitude,longitude,host_name,neighbourhood_group,price,room_type,last_review,license\n',
                'utf8'
            );
        }

        // 3. Ficheiro de bookings da cidade
        // Primeiro obtém IDs dos listings da cidade
        const cityListingIds = listingsWithCityId.map(listing => listing?.id).filter(id => id !== undefined);

        // Filtra bookings cujo listing_id está na lista de IDs da cidade
        const cityBookings = (data.bookings || []).filter(booking => {
            if (!booking || booking.listing_id === undefined) return false;
            return cityListingIds.includes(booking.listing_id);
        });

        // JSON
        fs.writeFileSync(
            path.join(downloadsDir, `${citySlug}_${countrySlug}_bookings.json`),
            JSON.stringify(cityBookings, null, 2),
            'utf8'
        );

        // CSV
        if (cityBookings.length > 0) {
            // Encontra todos os campos únicos
            const allKeys = new Set();
            cityBookings.forEach(booking => {
                if (booking && typeof booking === 'object') {
                    Object.keys(booking).forEach(key => allKeys.add(key));
                }
            });

            const headers = Array.from(allKeys).join(',');
            const rows = cityBookings.map(booking => {
                if (!booking) return '';
                return Array.from(allKeys).map(key => {
                    const value = booking[key];
                    if (value === undefined || value === null) return '';
                    if (typeof value === 'string') {
                        return `"${value.replace(/"/g, '""')}"`;
                    }
                    if (typeof value === 'object') {
                        return `"${JSON.stringify(value).replace(/"/g, '""')}"`;
                    }
                    return value;
                }).join(',');
            }).filter(row => row !== '');

            const bookingsCSV = [headers, ...rows].join('\n');
            fs.writeFileSync(
                path.join(downloadsDir, `${citySlug}_${countrySlug}_bookings.csv`),
                bookingsCSV,
                'utf8'
            );
        } else {
            // Cria ficheiro vazio com cabeçalhos básicos
            fs.writeFileSync(
                path.join(downloadsDir, `${citySlug}_${countrySlug}_bookings.csv`),
                'booking_id,listing_id,check_in,check_out,state,nr_guests\n',
                'utf8'
            );
        }

    });
});
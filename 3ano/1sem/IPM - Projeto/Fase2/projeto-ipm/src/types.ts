export type Coordinates = {
    lat: number
    lng: number
    zoom: number
}

export type City = {
    id?: number
    name: string
    slug: string
    country: string
    image?: string
    coords: Coordinates
}

export type Continent = {
    id?: number
    name: string
    cities: City[]
}

export type Listing = {
    id: number
    name: string
    price: number
    room_type: string
    neighbourhood_group: string
    host_name: string
    last_review?: string
    license?: string
    latitude: number
    longitude: number
    [key: string]: any
}

export type Booking = {
    id: number
    listing_id: number
    check_in: string
    check_out: string
    state: 'EXECUTED' | 'RESERVED' | 'CANCELLED'
    nr_guests: number
    [key: string]: any
}

export type DownloadFile = {
    id: number
    baseName: string
    formats: {
        csv: boolean
        json: boolean
    }
    description: string
}

export type FAQQuestion = {
    id: number
    question: string
    answer: string
}

export type FAQCategory = {
    id: number
    category: string
    questions: FAQQuestion[]
}

export type CityStats = {
    totalListings: number
    avgPrice: number
    totalBookings: number
    executedBookings: number
    totalBookedNights: number
    occupancyRate?: number
    avgStayDuration?: number
    totalGuests?: number
}

export type DashboardStats = CityStats & {
    roomTypes: string[]
    mainRoomType: string
    currentContinent: string
    listingQualityScore: number
    continentAvgPrice: number
    priceDifference: number
    cancellationRate: number
    estimatedRevenue: number
    avgRevenuePerBooking: number
    avgGuestsPerBooking: number
    peakMonths: string
    topHosts: Array<{ name: string; count: number }>
    mostPopularType: { type: string; percentage: number }
}

export type ComparisonInsight = {
    id: number
    icon: string
    title: string
    description: string
    value: string
    trend: 'higher' | 'lower' | 'similar' | 'different' | 'info'
    comparison: string
}

export type ContinentAverage = {
    [continent: string]: number
}

export type CityDisplayData = {
    city: City | null
    listings: Listing[]
    bookings: Booking[]
    stats: CityStats
}

export type DashboardData = {
    city: City | null
    listings: Listing[]
    bookings: Booking[]
    continents: Continent[]
    dashboardStats: DashboardStats
}

export type ComparisonData = CityDisplayData & {
    continents: Continent[]
    continentAverage: ContinentAverage
    comparisons: ComparisonInsight[]
}
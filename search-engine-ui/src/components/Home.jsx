import React from 'react'
import '../CSS/Home.css'

function Home() {
    return (
        <div class="search-box">
            <form action="#" method="get">
                <input type="text" name="search" placeholder="Search..." />
                <button type="submit"><i class="fa fa-search"></i></button>
            </form>
        </div>
    )
}

export default Home
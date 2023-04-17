import React, { useState } from 'react';
import '../CSS/Home2.css'
import '@fortawesome/fontawesome-free/css/all.min.css';
import { Link } from 'react-router-dom';


function Home() {
    const [text, setText] = useState('');

    const handleKeyPress = (event) => {
        if (event.key === 'Enter') {
            window.location.href = "./search";
        }
    };

    return (
        <div id='body'>
            <main>
                <img className='logo' src="https://www.google.com/images/branding/googlelogo/1x/googlelogo_color_272x92dp.png" alt="" />
                <div className='search'>
                    <Link to="/search"><span className='fas fa-search'></span></Link>
                    <input type="text" value={text} onChange={(e) => setText(e.target.value)} onKeyPress={handleKeyPress} />
                    <span className="fas fa-microphone"></span>
                </div>
            </main>
        </div>
    )
}

export default Home


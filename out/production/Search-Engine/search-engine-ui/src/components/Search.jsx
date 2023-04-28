import React, { useState } from 'react';
import WebsiteContainer from './WebsiteContainer';
import { Link } from 'react-router-dom';
import '../CSS/Search.css';
import '@fortawesome/fontawesome-free/css/all.min.css';
import data from '../data';

function Search() {
    const [text, setText] = useState('');

    const handleKeyPress = (event) => {
        if (event.key === 'Enter') {
            window.location.href = "./search";
        }
    };

    const [currentPage, setCurrentPage] = useState(1);
    const itemsPerPage = 10;

    const startIndex = (currentPage - 1) * itemsPerPage;
    const endIndex = startIndex + itemsPerPage;
    const currentData = data.slice(startIndex, endIndex);

    const totalPages = Math.ceil(data.length / itemsPerPage);

    const handlePageChange = (pageNumber) => {
        setCurrentPage(pageNumber);
    };

    return (
        <div id="holder">
            <div className="search">
                <Link to="/search">
                    <span className="fas fa-search"></span>
                </Link>
                <input
                    type="text"
                    value={text}
                    onChange={(e) => setText(e.target.value)}
                    onKeyPress={handleKeyPress}
                />
                <span className="fas fa-microphone"></span>
            </div>

            {currentData.map((item) => (
                <WebsiteContainer
                    key={item.title}
                    title={item.title}
                    url={item.url}
                    queryParagraph={item.queryParagraph}
                />
            ))}

            <div className="pagination">
                {Array.from({ length: totalPages }, (_, index) => (
                    <button
                        key={index}
                        className={currentPage === index + 1 ? 'active' : ''}
                        onClick={() => handlePageChange(index + 1)}
                    >
                        {index + 1}
                    </button>
                ))}
            </div>
        </div>
    );
}

export default Search;

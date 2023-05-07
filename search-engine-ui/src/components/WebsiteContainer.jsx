import React from "react";
import "../CSS/WebsiteContainer.css"

function WebsiteContainer({ url, title, queryParagraph }) {
    return (
        <div id="webcontainer">
            <a href={url}>{title}</a>
            <h2>{url}</h2>
            <p>{queryParagraph} </p>
        </div>
    );
}

export default WebsiteContainer;
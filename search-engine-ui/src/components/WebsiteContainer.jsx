import React from "react";
import "../CSS/WebsiteContainer.css"

function WebsiteContainer(props) {
    return (
        <div id="webcontainer">
            <a href={props.url}>{props.title}</a>
            <h2>{props.url}</h2>
            <p>{props.queryParagraph} </p>
        </div>
    );
}

export default WebsiteContainer;
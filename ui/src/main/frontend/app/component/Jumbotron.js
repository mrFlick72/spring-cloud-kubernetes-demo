import React from "react"
import TextInputForm from "./TextInputForm";

export default ({title, leadSection, bottomSection}) => {
    return <div className="jumbotron">
        <h1 className="display-4">{title}</h1>

        <div className="lead">
            {leadSection}
        </div>

        <hr className="my-4"/>

        {bottomSection}
    </div>
}

import React from "react"
import TextInputForm from "./TextInputForm";

export default ({title, submitFn, inputRef, message}) => {
    console.log(inputRef.current)
    return <div className="jumbotron">
        <h1 className="display-4">{title}</h1>
        <form className="lead">
            <TextInputForm inputRef={inputRef}
                           componentId="name"
                           componentLabel="Say Hello to "
                           componentPlaceholder="Say Hello to "/>
            <button type="button" onClick={submitFn} className="btn btn-primary">Submit</button>
        </form>
        <hr className="my-4"/>

        <p>{message}</p>
    </div>
}

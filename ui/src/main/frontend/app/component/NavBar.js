import React from "react"

export default ({title}) => (
    <div className="container-fluid">
        <nav className="navbar fixed-top navbar-expand-lg navbar-dark bg-dark">
            <button className="navbar-toggler" type="button" data-toggle="collapse" data-target="#navBar"
                    aria-controls="navbarToggler" aria-expanded="false" aria-label="Toggle navigation">
                <span className="navbar-toggler-icon"></span>
            </button>
            <div className="collapse navbar-collapse" id="navBar">
                <a className="navbar-brand" href="#">{title}</a>
                <ul className="navbar-nav mr-auto mt-2 mt-lg-0" />
                <form action="/logout" method="post" className="form-inline my-2 my-lg-0">
                    <button type="submit" className="btn btn-secondary"> Logout <i className="fas fa-sign-out-alt fs-lg"></i></button>
                </form>
            </div>
        </nav>
    </div>
);
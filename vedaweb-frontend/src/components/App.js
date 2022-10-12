import React, { Component } from "react";
import { BackTop, Spin, Icon } from 'antd';

import NavBar from './general/NavBar';
import SearchView from './search/SearchView';
import ContentView from './display/ContentView';
import About from './static/About';
import Help from './help/Help';
import NotFound from './errors/NotFound';
import ErrorBoundary from './errors/ErrorBoundary';
import Footer from './general/Footer';
import System from './utils/System';
import SiteNotice from './static/SiteNotice';
import PrivacyPolicy from './static/PrivacyPolicy';

import GuidedTour from './help/GuidedTour';

import "./App.css";

import stateStore from "../stateStore";
import { view } from 'react-easy-state';

import { Route, Switch, withRouter } from 'react-router-dom';
import SearchResults from "./search/SearchResults";

import Sanscript from 'sanscript';
import axios from 'axios';

import "./utils/polyfills";

import { unregister } from '../registerServiceWorker';


class App extends Component {

    constructor(props){
        super(props);

        //unregister service worker
        unregister();

        this.state = {
            isLoaded: false,
            error: undefined
        };

        //load ui data
        this.loadUiData();

        //configure iso scheme for sanscript.js
        let iso = JSON.parse(JSON.stringify(Sanscript.schemes.iast));
        iso.vowels = 'a ā i ī u ū r̥ r̥̄ l̥ l̥̄ ē e ai ō o au'.split(' ');
        iso.consonants = 'k kh g gh ṅ c ch j jh ñ ṭ ṭh ḍ ḍh ṇ t th d dh n p ph b bh m y r l v ś ṣ s h ḷ kṣ jñ'.split(' ');
        iso.other_marks = ['ṁ', 'ḥ', '~'];
        Sanscript.addRomanScheme('iso', iso);

        //set global default spinner indicator
        Spin.setDefaultIndicator(<Icon type="loading" spin style={{ fontSize: 38 }}/>);
    }

    componentCleanup(){
        //save settings from state store
        stateStore.save(stateStore);
    }

    componentDidMount(){
        //load local storage data (if any)
        stateStore.load(stateStore);
        //add listener to call cleanup before site closes
        window.addEventListener('beforeunload', this.componentCleanup);
    }

    componentWillUnmount() {
        //exec cleanup
        this.componentCleanup();
        //remove listener for cleanup
        window.removeEventListener('beforeunload', this.componentCleanup);

        //remove listener for location changes
        this.props.history.unlisten();
    }


    loadUiData(){
        axios.get(process.env.PUBLIC_URL + "/api/uidata")
        .then((response) => {
            // assign all received ui data to state store ui prop
            Object.assign(stateStore.ui, response.data);
            //stateStore.ui.meta = response.data.meta;
            //stateStore.ui.abbreviations = response.data.abbreviations;
            //stateStore.ui.layers = response.data.layers;
            //stateStore.ui.snippets = response.data.snippets;
            //stateStore.ui.help = response.data.help;
            this.setState({ error: undefined });
        })
        .catch((error) => {
            this.setState({ error: error });
        })
        .finally(() => {
            this.setState({ isLoaded: true });
        });
    }


    render() {

        const { error, isLoaded } = this.state;

        if (isLoaded && error)
            console.log(JSON.stringify(error));

        return (

                <div id="app">

                    { !isLoaded &&
                        <div style={{
                        display:"flex",
                        justifyContent:"center",
                        alignItems:"center",
                        height:"100vh",
                        width:"100%" }}>
                            <Spin
                            size="large"
                            spinning={!isLoaded} />
                        </div>
                    }

                    {/* ERROR MESSAGE: FRONTEND UI DATA COULD NOT BE LOADED */}
                    { isLoaded && error &&
                        <div className="error-msg">
                            <Icon type="frown-o" className="gap-right"/>
                            There was an error loading the application data.
                            This could be due to a temporary server problem.
                        </div>
                    }

                    { isLoaded && !error &&
                        <ErrorBoundary>

                            <NavBar />

                            <Switch>
                                <Route path="/view/:by/:value" component={ContentView} />
                                <Route path="/view" component={ContentView} />
                                <Route path="/results/:querydata" component={SearchResults} />
                                <Route path="/search" component={SearchView} />
                                <Route path="/system/:auth" component={System} />
                                <Route path="/sitenotice" component={SiteNotice} />
                                <Route path="/privacypolicy" component={PrivacyPolicy} />
                                <Route path="/about" component={About} />
                                <Route path="/help" component={Help} />
                                <Route path="/home" component={ContentView} />
                                <Route path="/" exact={true} component={ContentView} />
                                <Route component={NotFound} />
                            </Switch>

                            <Footer/>

                            <BackTop />

                        </ErrorBoundary>
                    }

                    <GuidedTour
                    enabled={stateStore.settings.tour}
                    onCloseTour={() => stateStore.settings.tour = false}/>

                </div>

        );
    }
}

export default withRouter(view(App));

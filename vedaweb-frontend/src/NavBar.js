import React, { Component } from "react";
import { Row, Col, Icon, Menu } from 'antd';

import SearchSmart from "./SearchSmart";
import logo from "./img/logo.png";
import "./css/NavBar.css";

import searchMetaStore from "./stores/searchMetaStore";

import { view } from 'react-easy-state';

import { NavLink } from 'react-router-dom'

const SubMenu = Menu.SubMenu;


class NavBar extends Component {


    render() {

        const menuStyle = {
            backgroundColor: "transparent",
            borderBottom: "none"
        };

        return (
            
            <Row
            type="flex"
            align="middle"
            id="navbar"
            className="box-shadow">

                {/* <Col
                span={4}
                className="content-left"> */}
                    <NavLink to={"/home"} className="v-middle">
                        <img src={logo} className="navbar-logo" alt="" />
                        <div className="navbar-app-title">
                            <span className="bold">VedaWeb</span><br/>
                            Rigveda online
                        </div>
                    </NavLink>
                {/* </Col> */}

                {/* <Col span={8}> */}
                <div className="flex-grow-1">
                    <SearchSmart />
                </div>
                {/* </Col> */}

                {/* <Col span={12}> */}
                    <div className="flex-grow-2">
                        <Menu
                        selectedKeys={[]}
                        mode="horizontal"
                        style={menuStyle}>
                            
                            <Menu.Item key="search">
                                <NavLink to={"/search"}>
                                    {/* <Icon type="search"/> */}
                                    Advanced Search
                                </NavLink>
                            </Menu.Item>

                            <SubMenu
                            title={<span><Icon type="book"/>Browse Rigveda</span>}
                            className="right">
                                {searchMetaStore.scopeDataRaw.map((hymns, i) => (
                                    <Menu.Item key={'view_' + i}>
                                        <NavLink to={"/view/id/" + (i+1) + ".1.1"}>
                                            Book {('0' + (i+1)).slice(-2)} ({hymns} Hymns)
                                        </NavLink>
                                    </Menu.Item>
                                ))}
                            </SubMenu>

                        </Menu>
                    </div>
                {/* </Col> */}

            </Row>
            
        );
    }
}

export default view(NavBar);
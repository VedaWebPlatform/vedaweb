import React, { Component } from "react";
import { Row, Col, Select } from 'antd';

import './css/SearchScope.css';

import { view } from 'react-easy-state';
import searchMetaStore from "./stores/searchMetaStore";

const Option = Select.Option;


class SearchScope extends Component {

    render() {

        const { fromBookRange, fromHymnRange, toBookRange, toHymnRange } = searchMetaStore.scope.ranges;
        const { fromBook, fromHymn, toBook, toHymn } = searchMetaStore.scope.settings;

        return (

            <div>
           
                <Row type="flex" align="middle" className="top-gap bottom-gap-small">

                    {/* <Col span={8}>
                        <Icon type="eye-o" className="gap-right"/>
                        <span className="bold">Search Scope:</span>
                    </Col> */}

                    <Col span={3} className="search-input-label content-right">
                        From Book:
                    </Col>

                    <Col span={5}>
                        <Select
                        value={fromBook}
                        defaultValue={0}
                        className="search-scope-select-book"
                        onSelect={(value, o) => searchMetaStore.setScopeFromBook(value)}>
                            <Option
                            key={'fromBook_all'}
                            value={0}>
                                {'all'}
                            </Option>
                            {fromBookRange.map((book, i) => (
                                <Option
                                key={'fromBook_' + book.display}
                                value={book.value}>
                                    {book.display}
                                </Option>
                            ))}
                        </Select>
                    </Col>

                    <Col span={3} className="search-input-label content-right">
                        ... Hymn:
                    </Col>

                    <Col span={5}>
                        <Select
                        value={fromHymn}
                        defaultValue={0}
                        className="search-scope-select-book"
                        onSelect={(value, o) => searchMetaStore.setScopeFromHymn(value)}
                        disabled={fromBook === 0}>
                            <Option
                            key={'fromHymn_all'}
                            value={0}>
                                {'all'}
                            </Option>
                            {fromHymnRange.map((hymn) => (
                                <Option
                                key={'fromHymn_' + hymn.display}
                                value={hymn.value}>
                                    {hymn.display}
                                </Option>
                            ))}
                        </Select>
                    </Col>

                </Row>

                <Row type="flex" align="middle">

                    {/* <Col span={8} className="search-scope-indicator-container">
                        <SearchScopeIndicator
                            fromBook={fromBook}
                            fromHymn={fromHymn}
                            toBook={toBook}
                            toHymn={toHymn} />
                    </Col> */}

                    <Col span={3} className="search-input-label content-right">
                        To Book:
                    </Col>

                    <Col span={5}>
                        <Select
                        value={toBook}
                        defaultValue={0}
                        className="search-scope-select-book"
                        onSelect={(value, o) => searchMetaStore.setScopeToBook(value)}
                        disabled={fromBook === 0}>
                            <Option
                            key={'toBook_all'}
                            value={0}>
                                {'all'}
                            </Option>
                            {toBookRange.map((book) => (
                                <Option
                                key={'toBook_' + book.display}
                                value={book.value}>
                                    {book.display}
                                </Option>
                            ))}
                        </Select>
                    </Col>

                    <Col span={3} className="search-input-label content-right">
                        ... Hymn:
                    </Col>

                    <Col span={5}>
                        <Select
                        value={toHymn}
                        defaultValue={0}
                        className="search-scope-select-book"
                        onSelect={(value, o) => searchMetaStore.setScopeToHymn(value)}
                        disabled={toBook === 0}>
                            <Option
                            key={'toHymn_all'}
                            value={0}>
                                {'all'}
                            </Option>
                            {toHymnRange.map((hymn) => (
                                <Option
                                key={'toHymn_' + hymn.display}
                                value={hymn.value}>
                                    {hymn.display}
                                </Option>
                            ))}
                        </Select>
                    </Col>

                </Row>

            </div>
               
        );

    }
}

export default view(SearchScope);
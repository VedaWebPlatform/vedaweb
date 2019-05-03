import React, { Component } from "react";
import { Drawer, Icon, Button, Radio } from 'antd';
import axios from 'axios';
import fileDownload from "js-file-download";

const RadioGroup = Radio.Group;
const exportOptions = ["XML"];

class ExportDrawer extends Component {


    constructor(props){
        super(props);
        this.state = { format: exportOptions[0], isExportLoaded: true };
        this.export = this.export.bind(this);
    }


    export() {
        this.setState({ isExportLoaded: false });

        // let query = {
        //     docId: this.props.docId,
        //     format: this.state.format,
        //     layers: this.props.layers
        // }

        axios.get(process.env.PUBLIC_URL + "/api/export/doc/" + this.props.docId + "/" + this.state.format.toLowerCase())
            .then((response) => {
                this.setState({
                    isExportLoaded: true
                });
                fileDownload(response.data, ("vedaweb-" + this.props.docId + "." + this.state.format.toLowerCase()));
            })
            .catch((error) => {
                this.setState({
                    isExportLoaded: true
                });
                alert("There was an error generating the data.");
            });
    }


    render() {
        
        return (
            
            <Drawer
            title={<h1 style={{marginBottom:'0'}}><Icon type="export" className="gap-right"/>Export</h1>}
            placement="right"
            width="auto"
            closable={true}
            onClose={this.props.onClose}
            visible={this.props.visible} >
                
                <RadioGroup
                onChange={(e) => this.setState({ format: e.target.value }) }
                defaultValue={this.state.format}>
                    {exportOptions.map((eOpt, i) => (
                        <Radio
                        key={'eOpt_' + i}
                        value={eOpt}
                        style={{ display: 'block', height: '30px', lineHeight: '30px' }}>
                            {eOpt}
                        </Radio>
                    ))}
                </RadioGroup>
            
                <Button
                block
                type="primary"
                icon={this.state.isExportLoaded ? "download" : "loading"}
                style={{marginTop:'2rem'}}
                onClick={this.export}>
                    Export
                </Button>
                    
            </Drawer>
            
        );

    }
}

export default ExportDrawer;
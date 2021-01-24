import React  from 'react';
import { Modal, Button, Carousel } from 'antd';

class ShowPics extends React.Component{

  info = () => {
    Modal.info({
      title: '',
      iconType:'false',
      width: '800px',
      okText: "ok",
      content: (
        <div style={{width:650, height: 400, lineHeight:400, textAlign:"center"}}>
          <Carousel autoplay={true}> //autoplay=true 表示自动切换图片
            {
            this.props.pics.split(',').map((value,index) => {
              return <div><img style={{ maxWidth:600 ,maxHeight:400, margin:"0 auto" }} src={value}/></div>
            })
          }
          </Carousel>
        </div>
      ),
      onOk() {},
    });
  };

  constructor(props){
    super(props);
    this.state={ //是否禁用预览按钮，取决于是pics是否有值
      btnDisabled: this.props.pics? false: true
    }
  }



  render() {
    return (
      <div> //shape=circle表示按钮是圆的
        <Button disabled={this.state.btnDisabled} icon="picture" shape="circle" onClick={()=>{this.info()}} />
      </div>
    )
  }

}

export default ShowPics;

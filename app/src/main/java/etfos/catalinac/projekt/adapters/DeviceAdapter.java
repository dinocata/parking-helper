package etfos.catalinac.projekt.adapters;


import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;
import etfos.catalinac.projekt.R;
import etfos.catalinac.projekt.models.BtDevice;

public class DeviceAdapter extends BaseAdapter {
    Context ctx;
    ArrayList<BtDevice> deviceList;

    public DeviceAdapter(Context ctx, ArrayList<BtDevice> deviceList) {
        super();
        this.ctx = ctx;
        this.deviceList = deviceList;
    }

    @Override
    public int getCount() { return deviceList.size(); }

    @Override
    public Object getItem(int position) { return deviceList.get(position); }

    @Override
    public long getItemId(int position) { return position; }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = View.inflate(ctx, R.layout.list_item_device, null);
        }
        BtDevice current = deviceList.get(position);
        ImageView iVIcon = (ImageView) convertView.findViewById(R.id.ivDeviceIcon);
        TextView tvDeviceName = (TextView) convertView.findViewById(R.id.tvDeviceName);
        TextView tvAddress = (TextView) convertView.findViewById(R.id.tvAddress);

        iVIcon.setImageResource(current.getImgRes());
        tvDeviceName.setText(current.getName());
        String text = "MAC: " + current.getAddress();
        tvAddress.setText(text);
        return convertView;
    }
}
